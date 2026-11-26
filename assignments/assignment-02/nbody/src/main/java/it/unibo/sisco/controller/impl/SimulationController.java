package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Configuration;
import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.api.SimulationEvents;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.ExecutorsNBodySimulation;
import it.unibo.sisco.model.impl.simulation.FixedThreadNBodySimulation;
import it.unibo.sisco.model.impl.simulation.NThreadNBodySimulation;
import it.unibo.sisco.model.impl.simulation.SequentialNBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Default implementation of {@link Controller}: executes the steps of the
 * simulation one after the other and, if a view is present, displays the state
 * reached after each step.
 *
 * <p>
 * The view is initialised once at the beginning of {@link #run()}. Since the
 * view is updated between two steps, it always sees a consistent state of the
 * bodies.
 */
public final class SimulationController implements Controller {

    private enum ControllerState {
        TO_INIT,
        CONFIGURED,
        PAUSED,
        RUNNING,
        STEP,
        FINISHED
    }

    // Controller attributes
    private final BlockingQueue<SimulationEvents> eventQueue;
    private final Object stateLock;
    private Set<SimulationEvents> nextReceivableEvents;
    private ControllerState state;
    private Configuration config;

    // Model and View
    private NBodySimulation simulator;
    private SimulationView view;

    /**
     * Creates a controller for an N-body simulation.
     */
    public SimulationController() {
        this.stateLock = new Object();
        this.eventQueue = new LinkedBlockingQueue<>();
        this.nextReceivableEvents = Set.of(SimulationEvents.SET_CONFIGURATION);
        this.state = ControllerState.TO_INIT;
    }

    @Override
    public void run() {
        if (this.view != null) this.view.init();

        while (this.state != ControllerState.FINISHED) {
            if (this.state == ControllerState.RUNNING || this.state == ControllerState.STEP) {
                // If there is an event handle it, otherwise go on
                final SimulationEvents queuedEvent = this.eventQueue.poll();
                if (queuedEvent != null) {
                    this.processEvent(queuedEvent);
                }
            } else {
                // Wait for an event to arrive
                try {
                    this.processEvent(this.eventQueue.take());
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting for a simulation event.", e);
                }
            }

            if (this.state == ControllerState.STEP || this.state == ControllerState.RUNNING) {
                this.executeStep();
            }

            // Go back to pause if there was a single step
            if (this.state == ControllerState.STEP) {
                this.state = ControllerState.PAUSED;
            }
        }
        if (this.view != null) this.view.simulationOver(this.simulator.getTotalComputeNanos());
    }

    private void processEvent(final SimulationEvents event) {
        switch (event) {
            case SET_CONFIGURATION:
                this.configure();
                if (this.view != null) {
                    this.view.display(
                        this.simulator.getBodies(),
                        this.simulator.getTotalComputeNanos(),
                        this.simulator.getVirtualTime(),
                        this.simulator.getIteration()
                    );
                }
                this.state = ControllerState.CONFIGURED;
                break;
            case START:
                this.state = ControllerState.RUNNING;
                break;
            case PAUSE:
                this.state = ControllerState.PAUSED;
                break;
            case STEP:
                this.state = ControllerState.STEP;
                break;
        }
    }

    private void executeStep() {
        this.simulationStep();
        synchronized (this.stateLock) {
            if (this.simulator.getIteration() >= this.config.iterations()) {
                this.state = ControllerState.FINISHED;
                this.eventQueue.clear();
            }
        }
    }

    private void simulationStep() {
        this.simulator.step();
        if (this.view != null) {
            this.view.display(
                this.simulator.getBodies(),
                this.simulator.getTotalComputeNanos(),
                this.simulator.getVirtualTime(),
                this.simulator.getIteration()
            );
        }
    }

    @Override
    public void attachView(final SimulationView view) {
        if (this.view == null) {
            this.view = view;
        } else {
            throw new IllegalStateException("View already attached.");
        }
    }

    @Override
    public void handleEvent(final SimulationEvents event, final Optional<?> data) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(data);
        synchronized (this.stateLock) {
            if (this.state == ControllerState.FINISHED) {
                throw new IllegalStateException("The configured number of steps has been reached.");
            }
            // Check if the incoming event is in the allowed ones
            if (!this.nextReceivableEvents.contains(event)) {
                throw new IllegalStateException("Invalid event received: " + event);
            }
            // Handle the payload
            if (event == SimulationEvents.SET_CONFIGURATION) {
                if (data.isEmpty() || !(data.get() instanceof Configuration configuration)) {
                    throw new IllegalArgumentException("SET_CONFIGURATION requires a Configuration payload.");
                }
                this.config = configuration;
            } else {
                if (data.isPresent()) {
                    throw new IllegalArgumentException("Event '" + event + "' must have no payload.");
                }
            }
            this.eventQueue.add(event);
            this.nextReceivableEvents = event.getNextPossibleEvents();
        }
    }

    private void configure() {
        final List<Body> bodies = BodySets.random(this.config.bodies());
        final NBodySimulation configuredSimulator = switch (this.config.simulator()) {
            case SimulatorTypes.SEQUENTIAL -> new SequentialNBodySimulation(bodies);
            case SimulatorTypes.N_SYSTEM_THREAD -> new NThreadNBodySimulation(bodies);
            case SimulatorTypes.FIXED_SYSTEM_THREAD -> new FixedThreadNBodySimulation(bodies);
            case SimulatorTypes.EXECUTORS -> new ExecutorsNBodySimulation(bodies);
        };
        if (this.view != null)
            this.view.selectedConfiguration(this.config);
        this.simulator = configuredSimulator;
    }
}
