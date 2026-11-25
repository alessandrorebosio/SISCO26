package it.unibo.sisco.controller.api;

/**
 * Controller that drives an N-body simulation and keeps its view up to date.
 *
 * <p>
 * The steps are executed on a thread of the controller, so the commands return
 * immediately and can be given from any thread, the GUI one included. A command
 * that is not allowed in the current {@link ControlState} has no effect. The
 * simulation stops by itself after its configured number of steps.
 */
public interface Controller {

    /** Starts executing the steps one after the other. */
    void start();

    /** Suspends the simulation after the step in progress. */
    void pause();

    /** Resumes a suspended simulation. */
    void resume();

    /** Executes a single step, then suspends the simulation. */
    void step();

    /** Stops the simulation for good, after the step in progress. */
    void stop();

    /**
     * Waits until the simulation is stopped.
     *
     * @throws InterruptedException if the calling thread is interrupted while
     *                              waiting
     */
    void awaitTermination() throws InterruptedException;
}
