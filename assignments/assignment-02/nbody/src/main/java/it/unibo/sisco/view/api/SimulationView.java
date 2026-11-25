package it.unibo.sisco.view.api;

import it.unibo.sisco.model.api.SimulationState;

/**
 * A view that displays the stages of an N-body simulation.
 *
 * <p>
 * Its methods are called by the thread that drives the simulation, between two
 * steps.
 */
public interface SimulationView {

    /**
     * Prepares the view before the first step, for example resetting its
     * statistics.
     */
    void init();

    /**
     * Displays the current stage of the simulation.
     *
     * @param state a snapshot of the simulation, which the view can keep
     */
    void display(SimulationState state);

    /**
     * Notifies the view that the simulation has finished.
     *
     * @param timeElapsed the time taken by all the steps, drawing included, in
     *                    milliseconds
     */
    void simulationOver(long timeElapsed);
}
