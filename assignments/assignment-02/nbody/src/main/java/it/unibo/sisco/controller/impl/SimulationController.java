package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.view.api.SimulationView;
import java.util.Optional;
import java.util.stream.LongStream;

/**
 * Default implementation of {@link Controller}: executes the steps of the simulation one
 * after the other and, if a view is present, displays the state reached after each step.
 *
 * <p>The view is initialised once at the beginning of {@link #run(long)}. Since the view is
 * updated between two steps, it always sees a consistent state of the bodies.
 */
public final class SimulationController implements Controller {

    private final Optional<SimulationView> view;
    private final NBodySimulation simulation;

    /**
     * Builds a controller without a view, for example to measure the performance of the
     * simulation.
     *
     * @param simulation the simulation to drive
     */
    public SimulationController(final NBodySimulation simulation) {
        this.simulation = simulation;
        this.view = Optional.empty();
    }

    /**
     * Builds a controller that displays every stage on a view.
     *
     * @param simulation the simulation to drive
     * @param view the view to update at every step
     */
    public SimulationController(final NBodySimulation simulation, final SimulationView view) {
        this.simulation = simulation;
        this.view = Optional.of(view);
    }

    @Override
    public void run(final long nSteps) {
        view.ifPresent(SimulationView::init);
        LongStream.range(0, nSteps).forEach(i -> step());
    }

    private void step() {
        simulation.step();
        view.ifPresent(this::display);
    }

    private void display(final SimulationView v) {
        v.display(simulation.getBodies(), simulation.getVirtualTime(), simulation.getIteration());
    }
}
