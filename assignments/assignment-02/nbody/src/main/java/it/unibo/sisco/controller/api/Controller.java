package it.unibo.sisco.controller.api;

/**
 * Controller that drives an N-body simulation and, if present, keeps its view up to date.
 */
public interface Controller {

    /**
     * Runs the simulation for a given number of steps, in the calling thread.
     *
     * <p>The method returns only when every step has been executed.
     *
     * @param nSteps the number of steps to execute; nothing is executed if it is not positive
     */
    void run(long nSteps);
}
