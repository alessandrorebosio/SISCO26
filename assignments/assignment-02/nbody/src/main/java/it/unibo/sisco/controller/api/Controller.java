package it.unibo.sisco.controller.api;

/**
 * Controller that drives an N-body simulation and keeps its view up to date.
 *
 * <p>
 * The steps are executed on a thread of the controller, so the commands return
 * immediately and can be given from any thread, the GUI one included. The
 * simulation is over after its configured number of steps: from then on the
 * commands have no effect.
 */
public interface Controller {

    /**
     * Executes the steps one after the other, until {@link #stop()} is called or
     * the simulation is over. It has no effect if the steps are already running.
     */
    void start();

    /**
     * Pauses the simulation after the step in progress.
     */
    void stop();

    /**
     * Executes a single step. It has no effect while the steps are running.
     */
    void step();

    /**
     * Waits until the simulation is over.
     *
     * @throws InterruptedException if the calling thread is interrupted while
     *                              waiting
     */
    void awaitTermination() throws InterruptedException;
}
