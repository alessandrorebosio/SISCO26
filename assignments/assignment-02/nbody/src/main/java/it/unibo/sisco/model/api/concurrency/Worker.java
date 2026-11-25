package it.unibo.sisco.model.api.concurrency;

/**
 * An active component of a concurrent simulation: once started, it runs on its
 * own thread, synchronising with the other components, until the simulation is
 * over.
 */
public interface Worker {

    /**
     * Starts the worker on its own thread. It must be called only once.
     */
    void start();

    /**
     * Asks the worker to stop. It returns immediately: the worker leaves its
     * loop as soon as it notices the request, and it never blocks the others.
     */
    void stop();
}

