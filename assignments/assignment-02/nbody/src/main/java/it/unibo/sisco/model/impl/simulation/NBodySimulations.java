package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulationType;

import java.util.List;

/**
 * Factory of the N-body simulations: it has a method for each implementation,
 * with the default number of threads or a given one, and methods that choose
 * the implementation from a {@link SimulationType}.
 */
public final class NBodySimulations {

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
     * Builds the simulation of the given type, with the default number of
     * threads.
     *
     * @param type   the implementation to build
     * @param bodies the bodies of the simulation
     * @return the new simulation
     */
    public static NBodySimulation of(final SimulationType type, final List<? extends Body> bodies) {
        return switch (type) {
            case SEQUENTIAL -> sequential(bodies);
            case THREADS -> threads(bodies);
            case EXECUTORS -> executors(bodies);
        };
    }

    /**
     * Builds the parallel simulation of the given type, using the given number of
     * threads.
     *
     * @param type       {@link SimulationType#THREADS} or
     *                   {@link SimulationType#EXECUTORS}
     * @param bodies     the bodies of the simulation
     * @param numThreads the number of threads, that is the size of the pool for
     *                   {@link SimulationType#EXECUTORS}
     * @return the new simulation
     * @throws IllegalArgumentException if the type is
     *                                  {@link SimulationType#SEQUENTIAL} or if
     *                                  {@code numThreads} is not positive
     */
    public static NBodySimulation of(final SimulationType type, final List<? extends Body> bodies,
            final int numThreads) {
        return switch (type) {
            case THREADS -> threads(bodies, numThreads);
            case EXECUTORS -> executors(bodies, numThreads);
            case SEQUENTIAL -> throw new IllegalArgumentException(
                    "The " + type + " simulation does not take a number of threads");
        };
    }
}
