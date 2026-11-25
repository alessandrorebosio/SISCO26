package it.unibo.sisco.controller.impl;

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
import java.util.Objects;
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

    private static final int N_BODIES = 5000;
    private static final int N_STEPS = 100;
    private NBodySimulation simulator;
    private SimulationView view;

    /**
     * Creates a controller for an N-body simulation.
     */
    public SimulationController() {
    }

    @Override
    public void run() {
        Objects.requireNonNull(this.view, "No view attached");
        this.view.init();
        Objects.requireNonNull(this.simulator, "No simulator selected");

        final long startTime = System.nanoTime();
        for (int i = 0; i < N_STEPS; i++) {
            this.simulator.step();
            this.view.display(
                    this.simulator.getBodies(),
                    this.simulator.getVirtualTime(),
                    this.simulator.getIteration());
        }
        this.view.simulationOver(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
    }

    @Override
    public void attachView(final SimulationView view) {
        if (this.view != null) {
            throw new IllegalStateException("View already attached.");
        }
        this.view = view;
    }

    @Override
    public void setSimulator(final SimulatorTypes simulator) {
        final List<Body> bodies = BodySets.random(N_BODIES);
        this.simulator = switch (simulator) {
            case SimulatorTypes.SEQUENTIAL -> new SequentialNBodySimulation(bodies);
            case SimulatorTypes.N_SYSTEM_THREAD -> new NThreadNBodySimulation(bodies);
            case SimulatorTypes.FIXED_SYSTEM_THREAD -> new FixedThreadNBodySimulation(bodies);
            case SimulatorTypes.EXECUTORS -> new ExecutorsNBodySimulation(bodies);
        };
        if (this.view != null)
            this.view.selectedSimulator(simulator);
    }
}
