package it.unibo.sisco.controller.api;

import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.view.api.SimulationView;

/**
 * Controller that drives an N-body simulation and, if present, keeps its view
 * up to date.
 */
public interface Controller {

    /**
     * Runs the simulation for its configured number of steps, in the calling
     * thread.
     *
     * <p>
     * The method returns only when every step has been executed.
     */
    void run();

    /**
     * Attaches a view to receive simulation updates.
     *
     * @param view the view to attach
     */
    void attachView(SimulationView view);

    /**
     * Selects the simulation implementation to run.
     *
     * @param simulator the simulation implementation to use
     */
    void setSimulator(SimulatorTypes simulator);
}
