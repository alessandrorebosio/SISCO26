package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link Controller}: executes the steps of the
 * simulation one after the other and displays the state reached after each
 * step.
 *
 * <p>
 * The view is initialised once at the beginning of {@link #run()}. Since the
 * view is updated between two steps, it always sees a consistent state of the
 * bodies.
 */
public final class SimulationController implements Controller {

    private final SimulationView view;
    private final NBodySimulation simulation;
    private final int nSteps;

    /**
     * Creates a controller that runs the given simulation and displays it on
     * the given view.
     *
     * @param view       the view that displays the simulation
     * @param simulation the simulation to run
     * @param nSteps     the number of steps executed by {@link #run()}
     * @throws IllegalArgumentException if {@code nSteps} is not positive
     */
    public SimulationController(final SimulationView view, final NBodySimulation simulation, final int nSteps) {
        if (nSteps < 1) {
            throw new IllegalArgumentException("The number of steps must be positive: " + nSteps);
        }
        this.view = Objects.requireNonNull(view);
        this.simulation = Objects.requireNonNull(simulation);
        this.nSteps = nSteps;
    }

    @Override
    public void run() {
        this.view.init();
        final long startTime = System.nanoTime();
        for (int i = 0; i < this.nSteps; i++) {
            this.simulation.step();
            this.view.display(this.simulation.getState());
        }
        this.view.simulationOver(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
    }
}
