package it.unibo.sisco.controller.api;

import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

/**
 * Controller that drives an N-body simulation and keeps its view up to date.
 */
public interface Controller {

    /**
     * Runs the simulation for its configured number of steps, in the calling
     * thread.
     *
     * <p>
     * The method returns only when every step has been executed.
     *
     * @throws NullPointerException if no view is attached or no simulation is
     *                              set
     */
    void run();

    /**
     * Attaches the view that displays the simulation.
     *
     * @param view the view to attach
     * @throws IllegalStateException if a view is already attached
     */
    void attachView(SimulationView view);

    /**
     * Sets the simulation to run, replacing the previous one if any.
     *
     * @param simulation the simulation to run
     */
    void setSimulator(NBodySimulation simulation);
}
