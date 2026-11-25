package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;

import java.util.List;
import java.util.Locale;

/**
 * Factory of the N-body simulations, with one method for each implementation
 * and one that chooses the implementation by name.
 */
public final class NBodySimulations {

    /** Name of the sequential implementation. */
    public static final String SEQUENTIAL = "sequential";

    /** Name of the implementation based on platform threads. */
    public static final String THREADS = "threads";

    /** Name of the implementation based on the Executor framework. */
    public static final String EXECUTORS = "executors";

    private NBodySimulations() {
    }

    /**
     * Builds a simulation that computes every step in the calling thread.
     *
     * @param bodies the bodies of the simulation
     * @return the new simulation
     */
    public static NBodySimulation sequential(final List<? extends Body> bodies) {
        return new SequentialNBodySimulation(bodies);
    }

    /**
     * Builds a simulation that splits the bodies among one thread for each
     * available processor.
     *
     * @param bodies the bodies of the simulation
     * @return the new simulation
     */
    public static NBodySimulation threads(final List<? extends Body> bodies) {
        return new ThreadNBodySimulation(bodies);
    }

    /**
     * Builds a simulation that splits the bodies among at most the given number
     * of threads.
     *
     * @param bodies     the bodies of the simulation
     * @param numThreads the maximum number of threads
     * @return the new simulation
     * @throws IllegalArgumentException if {@code numThreads} is not positive
     */
    public static NBodySimulation threads(final List<? extends Body> bodies, final int numThreads) {
        return new ThreadNBodySimulation(bodies, numThreads);
    }

    /**
     * Builds a simulation that runs one task for each body on a pool with one
     * thread for each available processor.
     *
     * @param bodies the bodies of the simulation
     * @return the new simulation
     */
    public static NBodySimulation executors(final List<? extends Body> bodies) {
        return new ExecutorsNBodySimulation(bodies);
    }

    /**
     * Builds a simulation that runs one task for each body on a pool of the given
     * number of threads.
     *
     * @param bodies   the bodies of the simulation
     * @param poolSize the number of threads of the pool
     * @return the new simulation
     * @throws IllegalArgumentException if {@code poolSize} is not positive
     */
    public static NBodySimulation executors(final List<? extends Body> bodies, final int poolSize) {
        return new ExecutorsNBodySimulation(bodies, AbstractNBodySimulation.DEFAULT_DT, poolSize);
    }

    /**
     * Builds the simulation with the given name, ignoring case.
     *
     * @param name   {@value #SEQUENTIAL}, {@value #THREADS} or {@value #EXECUTORS}
     * @param bodies the bodies of the simulation
     * @return the new simulation
     * @throws IllegalArgumentException if the name is not one of the above
     */
    public static NBodySimulation byName(final String name, final List<? extends Body> bodies) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case SEQUENTIAL -> sequential(bodies);
            case THREADS -> threads(bodies);
            case EXECUTORS -> executors(bodies);
            default -> throw unknown(name);
        };
    }

    /**
     * Builds the parallel simulation with the given name, ignoring case, using
     * the given number of threads.
     *
     * @param name       {@value #THREADS} or {@value #EXECUTORS}
     * @param bodies     the bodies of the simulation
     * @param numThreads the number of threads, that is the size of the pool for
     *                   {@value #EXECUTORS}
     * @return the new simulation
     * @throws IllegalArgumentException if the name is not one of the above, also
     *                                  when it is {@value #SEQUENTIAL}, or if
     *                                  {@code numThreads} is not positive
     */
    public static NBodySimulation byName(final String name, final List<? extends Body> bodies, final int numThreads) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case THREADS -> threads(bodies, numThreads);
            case EXECUTORS -> executors(bodies, numThreads);
            case SEQUENTIAL -> throw new IllegalArgumentException(
                    "The " + SEQUENTIAL + " simulation does not take a number of threads");
            default -> throw unknown(name);
        };
    }

    private static IllegalArgumentException unknown(final String name) {
        return new IllegalArgumentException("Unknown simulation '" + name + "', expected "
                + SEQUENTIAL + ", " + THREADS + " or " + EXECUTORS);
    }
}
