package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Configuration;
import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.ExecutorsNBodySimulation;
import it.unibo.sisco.model.impl.simulation.SequentialNBodySimulation;
import it.unibo.sisco.model.impl.simulation.FixedThreadNBodySimulation;
import it.unibo.sisco.model.impl.simulation.NThreadNBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.LongStream;

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
    private final CountDownLatch configurationReady = new CountDownLatch(1);

    /**
     * Creates a controller for an N-body simulation.
     */
    public SimulationController() {
    }

    @Override
    public void run() {
        if (this.view != null)
            this.view.init();
        try {
            this.configurationReady.await();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        final long startTime = System.currentTimeMillis();
        LongStream.range(0, config.iterations()).forEach(this::simulationStep);
        final long elapsed = System.currentTimeMillis() - startTime;
        if (this.view != null)
            this.view.simulationOver(elapsed);
    }

    private void simulationStep(final long stepNumber) {
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
    public void setConfiguration(final Configuration config) {
        this.config = config;
        final List<Body> bodies = BodySets.random(config.bodies());
        this.simulator = switch (config.simulator()) {
            case SimulatorTypes.SEQUENTIAL -> new SequentialNBodySimulation(bodies);
            case SimulatorTypes.N_SYSTEM_THREAD -> new NThreadNBodySimulation(bodies);
            case SimulatorTypes.FIXED_SYSTEM_THREAD -> new FixedThreadNBodySimulation(bodies);
            case SimulatorTypes.EXECUTORS -> new ExecutorsNBodySimulation(bodies);
        };
        this.configurationReady.countDown();
        if (this.view != null) {
            this.view.selectedConfiguration(config);
        }
    }
}
