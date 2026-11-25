package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.ExecutorsNBodySimulation;
import it.unibo.sisco.model.impl.simulation.SequentialNBodySimulation;
import it.unibo.sisco.model.impl.simulation.ThreadNBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

import java.util.List;
import java.util.Optional;
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
    private static final int N_STEPS = 200;
    private Optional<SimulationView> view = Optional.empty();
    private Optional<NBodySimulation> simulator = Optional.empty();

    /**
     * Creates a controller for an N-body simulation.
     */
    public SimulationController() {
    }

    @Override
    public void run() {
        this.view.ifPresent(SimulationView::init);
        while (this.simulator.isEmpty()) { }
        final long startTime = System.currentTimeMillis();
        LongStream.range(0, N_STEPS).forEach(this::simulationStep);
        final long elapsed = System.currentTimeMillis() - startTime;
        this.view.ifPresent(v -> v.simulationOver(elapsed));
    }

    private void simulationStep(final long stepNumber) {
        this.simulator.ifPresent(NBodySimulation::step);
        this.view.ifPresent(v -> {
            v.display(
                this.simulator.get().getBodies(),
                this.simulator.get().getVirtualTime(),
                this.simulator.get().getIteration()
            );
        });
    }

    @Override
    public void attachView(final SimulationView view) {
        this.view.ifPresentOrElse(
            t -> { throw new IllegalStateException("View already attached."); },
            () -> this.view = Optional.of(view)
        );
    }

    @Override
    public void setSimulator(final SimulatorTypes simulator) {
        final List<Body> bodies = BodySets.random(N_BODIES);
        this.simulator = Optional.of(
            switch (simulator) {
                case SimulatorTypes.SEQUENTIAL -> new SequentialNBodySimulation(bodies);
                case SimulatorTypes.SYSTEM_THREAD -> new ThreadNBodySimulation(bodies);
                case SimulatorTypes.EXECUTORS -> new ExecutorsNBodySimulation(bodies);
            }
        );
        this.view.ifPresent(v -> v.selectedSimulator(simulator));
    }
}
