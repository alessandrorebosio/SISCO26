package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import java.util.List;

/**
 * Thread-based implementation of the N-body simulation using Java threads directly.
 * Each worker thread is responsible for one body and synchronizes with the other workers
 * between the two phases of each step.
 */
public final class ThreadNBodySimulation extends AbstractNBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;
    // TODO: Multiple worker threads, initially one per body, and a shared barrier for synchronization.

    /**
     * Builds a simulation over a set of bodies, with the default time step.
     *
     * @param bodies the bodies of the simulation
     */
    public ThreadNBodySimulation(final List<? extends Body> bodies) {
        this(bodies, DEFAULT_DT);
    }

    /**
     * Builds a simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    public ThreadNBodySimulation(final List<? extends Body> bodies, final double dt) {
        super(bodies, dt);
    }

    @Override
    protected void computeStep() {
        /*
         * TODO:
         * One step advances every worker by one step:
         * forEachWorker.step()
         */

        /*
         * TODO:
         * Each worker step must compute the body's velocity, wait at the shared barrier
         * for all other workers, update the body's position, and wait until all bodies
         * have completed the phase.
         *
         * Worker step {
         *      body.computeVelocity()
         *      barrier.await()
         *      body.computePosition()
         *      barrier.await()
         * }
         */
    }
}
