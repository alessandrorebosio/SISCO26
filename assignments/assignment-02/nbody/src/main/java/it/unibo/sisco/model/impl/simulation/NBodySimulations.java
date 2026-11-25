package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulationType;

import java.util.List;

/**
 * Factory of the N-body simulations, which chooses the implementation from a
 * {@link SimulationType}.
 */
public final class NBodySimulations {

    private NBodySimulations() {
    }

    /**
     * Builds the simulation of the given type, with the default time step.
     *
     * @param type       the implementation to build
     * @param bodies     the bodies of the simulation
     * @param numThreads the number of threads, that is the size of the pool for
     *                   {@link SimulationType#EXECUTORS}; it is ignored by
     *                   {@link SimulationType#SEQUENTIAL}
     * @return the new simulation
     * @throws IllegalArgumentException if the simulation is parallel and
     *                                  {@code numThreads} is not positive
     */
    public static NBodySimulation of(final SimulationType type, final List<? extends Body> bodies,
            final int numThreads) {
        final double dt = AbstractNBodySimulation.DEFAULT_DT;
        return switch (type) {
            case SEQUENTIAL -> new SequentialNBodySimulation(bodies, dt);
            case THREADS -> new ThreadNBodySimulation(bodies, dt, numThreads);
            case EXECUTORS -> new ExecutorsNBodySimulation(bodies, dt, numThreads);
        };
    }
}
