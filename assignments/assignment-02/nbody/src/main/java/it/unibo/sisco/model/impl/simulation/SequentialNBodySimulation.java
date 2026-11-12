package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import java.util.List;

/**
 * Sequential implementation of the N-body simulation: both phases of a step run in the
 * calling thread, one body after the other.
 */
public final class SequentialNBodySimulation extends AbstractNBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;

    /**
     * Builds a simulation over a set of bodies, with the default time step.
     *
     * @param bodies the bodies of the simulation
     */
    public SequentialNBodySimulation(final List<? extends Body> bodies) {
        this(bodies, DEFAULT_DT);
    }

    /**
     * Builds a simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    public SequentialNBodySimulation(final List<? extends Body> bodies, final double dt) {
        super(bodies, dt);
    }

    @Override
    protected void computeStep() {
        getBodies().forEach(this::updateVelocity);
        getBodies().forEach(this::updatePosition);
    }
}
