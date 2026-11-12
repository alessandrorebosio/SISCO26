package it.unibo.sisco.model.impl;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.InfiniteForceException;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.V2d;
import java.util.List;

/**
 * Simple sequential implementation of {@link NBodySimulation}.
 */
public final class SequentialNBodySimulation implements NBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;

    private final List<Body> bodies;
    private final double dt;
    private double vt;
    private long iteration;

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
     */
    public SequentialNBodySimulation(final List<? extends Body> bodies, final double dt) {
        this.bodies = List.copyOf(bodies);
        this.dt = dt;
    }

    @Override
    public void step() {
        /* update bodies velocity */
        for (final Body b : bodies) {
            final V2d totalForce = computeTotalGForceOnBody(b);
            final V2d acc = totalForce.scalarMul(1.0 / b.getMass());
            b.updateVelocity(acc, dt);
        }
        /* compute bodies new pos */
        for (final Body b : bodies) {
            b.updatePos(dt);
        }
        /* update virtual time */
        vt += dt;
        iteration++;
    }

    @Override
    public List<Body> getBodies() {
        return bodies;
    }

    @Override
    public int getNumBodies() {
        return bodies.size();
    }

    @Override
    public double getVirtualTime() {
        return vt;
    }

    @Override
    public long getIteration() {
        return iteration;
    }

    /*
     * Computes the total gravitational force exerted on a body by the other bodies.
     */
    private V2d computeTotalGForceOnBody(final Body b) {
        V2d totalForce = V2d.ZERO;
        for (final Body otherBody : bodies) {
            if (!b.equals(otherBody)) {
                try {
                    totalForce = totalForce.sum(b.computeGForceBy(otherBody));
                } catch (final InfiniteForceException ignored) {
                    // bodies in the same position: the force is ignored, as in the reference version
                }
            }
        }
        return totalForce;
    }
}
