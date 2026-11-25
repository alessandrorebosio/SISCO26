package it.unibo.sisco.model.api;

/**
 * Available implementations for running an N-body simulation.
 */
public enum SimulatorTypes {
    /**
     * Runs the simulation sequentially.
     */
    SEQUENTIAL("Sequential"),
    /**
     * Runs the simulation using dedicated system threads. TODO
     */
    N_SYSTEM_THREAD("N. system thread"),
    /**
     * Runs the simulation using dedicated system threads. TODO
     */
    FIXED_SYSTEM_THREAD("Fixed system thread"),
    /**
     * Runs the simulation using the Java Executor Framework.
     */
    EXECUTORS("Executor framework");

    private final String label;

    private SimulatorTypes(final String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
