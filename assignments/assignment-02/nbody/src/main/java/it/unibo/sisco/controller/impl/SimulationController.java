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
import java.util.concurrent.TimeUnit;

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

    private SimulationView view;
    private NBodySimulation simulator;
    private Configuration config;
    private final Object stateLock = new Object();
    private Set<SimulationEvents> nextEvents = Set.of(SimulationEvents.SET_CONFIGURATION);
    private boolean running;
    private boolean finished;
    private int pendingSteps;
    private long stepsCompleted;
    private boolean stepInProgress;

    /**
     * Creates a controller for an N-body simulation.
     */
    public SimulationController() {
    }

    @Override
    public void run() {
        if (this.view != null)
            this.view.init();
        synchronized (this.stateLock) {
            while (this.config == null) {
                try {
                    this.stateLock.wait();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        if (this.view != null) {
            this.view.display(
                this.simulator.getBodies(),
                this.simulator.getVirtualTime(),
                this.simulator.getIteration()
            );
        }
        long startTime = 0;
        boolean simulationStarted = false;
        while (true) {
            final boolean singleStep;
            synchronized (this.stateLock) {
                while (!this.running && this.pendingSteps == 0 && !this.finished) {
                    try {
                        this.stateLock.wait();
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (this.finished)
                    break;
                singleStep = this.pendingSteps > 0;
                if (singleStep)
                    this.pendingSteps--;
                this.stepInProgress = true;
            }

            if (!simulationStarted) {
                startTime = System.nanoTime();
                simulationStarted = true;
            }
            this.simulationStep();

            synchronized (this.stateLock) {
                this.stepInProgress = false;
                this.stepsCompleted++;
                if (this.stepsCompleted >= this.config.iterations()) {
                    this.finished = true;
                    this.running = false;
                    this.pendingSteps = 0;
                }
                this.stateLock.notifyAll();
            }
        }
        final long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        if (this.view != null)
            this.view.simulationOver(elapsed);
    }

    private void simulationStep() {
        this.simulator.step();
        if (this.view != null) {
            this.view.display(
                this.simulator.getBodies(),
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
            if (this.finished) {
                throw new IllegalStateException("The configured number of steps has been reached.");
            }
            if (!this.nextEvents.contains(event)) {
                throw new IllegalStateException("Invalid event received: " + event);
            }
            switch (event) {
                case SET_CONFIGURATION -> {
                    if (data.isEmpty() || !(data.get() instanceof Configuration configuration)) {
                        throw new IllegalArgumentException("SET_CONFIGURATION requires a Configuration payload.");
                    }
                    this.configure(configuration);
                }
                case START -> this.running = true;
                case PAUSE -> this.running = false;
                case STEP -> {
                    final long outstandingSteps = this.stepsCompleted + this.pendingSteps + (this.stepInProgress ? 1 : 0);
                    if (outstandingSteps >= this.config.iterations()) {
                        throw new IllegalStateException("The configured number of steps has been reached.");
                    }
                    this.pendingSteps++;
                }
            }
            this.nextEvents = event.getNextPossibleEvents();
            this.stateLock.notifyAll();
        }
    }

    private void configure(final Configuration config) {
        Objects.requireNonNull(config);
        final List<Body> bodies = BodySets.random(config.bodies());
        final NBodySimulation configuredSimulator = switch (config.simulator()) {
            case SimulatorTypes.SEQUENTIAL -> new SequentialNBodySimulation(bodies);
            case SimulatorTypes.N_SYSTEM_THREAD -> new NThreadNBodySimulation(bodies);
            case SimulatorTypes.FIXED_SYSTEM_THREAD -> new FixedThreadNBodySimulation(bodies);
            case SimulatorTypes.EXECUTORS -> new ExecutorsNBodySimulation(bodies);
        };
        if (this.view != null)
            this.view.selectedConfiguration(config);
        this.simulator = configuredSimulator;
        this.config = config;
        this.stepsCompleted = 0;
    }
}
