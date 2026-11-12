package it.unibo.sisco.controller.api;

/**
 * Controller that drives an N-body simulation and, if present, its view.
 */
public interface Controller {

    /**
     * Runs the simulation for a given number of steps.
     *
     * @param nSteps the number of steps to execute
     */
    void run(long nSteps);
}
