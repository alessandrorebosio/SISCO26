package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.ExecutorsNBodySimulation;
import it.unibo.sisco.model.impl.simulation.SequentialNBodySimulation;
import it.unibo.sisco.model.impl.simulation.ThreadNBodySimu;
import it.unibo.sisco.view.api.SimulationView;

import java.util.List;
import java.util.stream.LongStream;

/**
 * Default implementation of {@link Controller}: executes the steps of the simulation one
 * after the other and, if a view is present, displays the state reached after each step.
 *
 * <p>The view is initialised once at the beginning of {@link #run()}. Since the view is
 * updated between two steps, it always sees a consistent state of the bodies.
 */
public final class SimulationController implements Controller {

    private static final int N_BODIES = 5000;
    private static final int N_STEPS = 100;
    private SimulationView view;
    private NBodySimulation simulator;

    /**
     * Creates a controller for an N-body simulation.
     */
    public SimulationController() {
    }

    @Override
    public void run() {
        if (this.view != null) this.view.init();
        while (this.simulator == null) { }
        final long startTime = System.currentTimeMillis();
        LongStream.range(0, N_STEPS).forEach(this::simulationStep);
        final long elapsed = System.currentTimeMillis() - startTime;
        if (this.view != null) this.view.simulationOver(elapsed);
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
    public void setSimulator(final SimulatorTypes simulator) {
        final List<Body> bodies = BodySets.random(N_BODIES);
        this.simulator = switch (simulator) {
            case SimulatorTypes.SEQUENTIAL -> new SequentialNBodySimulation(bodies);
            case SimulatorTypes.SYSTEM_THREAD -> new ThreadNBodySimu(bodies);
            case SimulatorTypes.EXECUTORS -> new ExecutorsNBodySimulation(bodies);
        };
        if (this.view != null) this.view.selectedSimulator(simulator);
    }
}
