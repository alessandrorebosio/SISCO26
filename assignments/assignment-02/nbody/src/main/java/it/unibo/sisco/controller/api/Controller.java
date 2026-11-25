package it.unibo.sisco.controller.api;

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
     */
    void run();
}
