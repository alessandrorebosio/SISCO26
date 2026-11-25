package it.unibo.sisco.view.api;

import it.unibo.sisco.controller.api.ControlState;
import it.unibo.sisco.controller.api.Controller;
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
     * Gives the view the controller that receives the commands of the user, if
     * the view has any. It is called once, before the simulation starts.
     *
     * @param controller the controller of the simulation
     */
    void setController(Controller controller);

    /**
     * Notifies the view that the state of the simulation has changed, so that it
     * can show which commands are allowed. It may be called from any thread,
     * also while the caller holds a lock: it must return without waiting.
     *
     * @param state the new state
     */
    void controlChanged(ControlState state);

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
