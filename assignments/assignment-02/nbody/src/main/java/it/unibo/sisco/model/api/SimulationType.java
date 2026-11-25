package it.unibo.sisco.model.api;

/**
 * The implementations of {@link NBodySimulation} that can be chosen.
 */
public enum SimulationType {

    /** Computes every step in the calling thread. */
    SEQUENTIAL,

    /** Splits the bodies among platform threads. */
    THREADS,

    /** Runs one task for each body on a pool of the Executor framework. */
    EXECUTORS
}
