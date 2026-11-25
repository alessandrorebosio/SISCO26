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

    private static final int N_STEPS = 100;
    private SimulationView view;
    private NBodySimulation simulation;

    /**
     * Creates a controller with no view and no simulation: they must be given
     * with {@link #attachView} and {@link #setSimulator} before {@link #run()}.
     */
    public SimulationController() {
        // The view and the simulation are given later
    }

    /**
     * Creates a controller that runs the given simulation and displays it on
     * the given view.
     *
     * @param view       the view that displays the simulation
     * @param simulation the simulation to run
     */
    public SimulationController(final SimulationView view, final NBodySimulation simulation) {
        this.attachView(view);
        this.setSimulator(simulation);
    }

    @Override
    public void run() {
        Objects.requireNonNull(this.view, "No view attached");
        Objects.requireNonNull(this.simulation, "No simulation set");

        this.view.init();
        final long startTime = System.nanoTime();
        for (int i = 0; i < N_STEPS; i++) {
            this.simulation.step();
            this.view.display(
                    this.simulation.getBodies(),
                    this.simulation.getVirtualTime(),
                    this.simulation.getIteration());
        }
        this.view.simulationOver(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
    }

    @Override
    public void attachView(final SimulationView newView) {
        if (this.view != null) {
            throw new IllegalStateException("View already attached");
        }
        this.view = Objects.requireNonNull(newView);
    }

    @Override
    public void setSimulator(final NBodySimulation newSimulation) {
        this.simulation = Objects.requireNonNull(newSimulation);
    }
}
