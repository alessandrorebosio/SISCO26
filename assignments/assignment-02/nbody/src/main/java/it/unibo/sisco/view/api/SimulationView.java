package it.unibo.sisco.view.api;

import it.unibo.sisco.model.api.Body;
import java.util.List;

/**
 * A view that displays the stages of an N-body simulation.
 */
public interface SimulationView {

    /**
     * Prepares the view before the simulation starts.
     */
    void init();

    /**
     * Displays the current stage of the simulation.
     *
     * @param bodies the bodies to display
     * @param vt the current virtual time
     * @param iteration the number of steps executed so far
     */
    void display(List<Body> bodies, double vt, long iteration);
}
