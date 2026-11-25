package it.unibo.sisco.model.api;

/**
 * Available implementations for running an N-body simulation.
 */
public enum SimulatorTypes {
    /**
     * Runs the simulation sequentially.
     */
    SEQUENTIAL,
    /**
     * Runs the simulation using dedicated system threads. TODO
     */
    N_SYSTEM_THREAD,
    /**
     * Runs the simulation using dedicated system threads. TODO
     */
    FIXED_SYSTEM_THREAD,
    /**
     * Runs the simulation using the Java Executor Framework.
     */
    EXECUTORS
}
