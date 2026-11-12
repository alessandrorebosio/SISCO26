package it.unibo.sisco.view.api;

import it.unibo.sisco.model.api.Body;
import java.util.List;

/**
 * A view that displays the stages of an N-body simulation.
 *
 * <p>Its methods are called by the thread that drives the simulation, between two steps.
 */
public interface SimulationView {

    /**
     * Prepares the view before the first step, for example resetting its statistics.
     */
    void init();

    /**
     * Displays the current stage of the simulation.
     *
     * <p>The bodies are the live ones of the model: the view must read them before
     * returning and must not keep any reference to them, since they change at the next step.
     *
     * @param bodies the bodies to display
     * @param vt the current virtual time
     * @param iteration the number of steps executed so far
     */
    void display(List<Body> bodies, double vt, long iteration);
}
