package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.exceptions.InfiniteForceException;
import it.unibo.sisco.model.impl.geometry.Vector2D;

import java.util.List;

/**
 * Skeleton of an {@link NBodySimulation}: it holds the bodies and the virtual time, and
 * provides the per-body operations of a step.
 *
 * <p>A step has two phases, and subclasses decide how to execute them (sequentially, with
 * threads, with tasks, ...):
 * <ol>
 *   <li>{@link #updateVelocity(Body)} on every body: it reads the positions of all the bodies
 *       and writes only the velocity of its own body;</li>
 *   <li>{@link #updatePosition(Body)} on every body: it reads and writes only its own body.</li>
 * </ol>
 * Within a phase, the operations on different bodies are independent of each other, while
 * the second phase must start only after the first one is complete on every body.
 */
public abstract class AbstractNBodySimulation implements NBodySimulation {

    private final List<Body> bodies;
    private final double dt;
    private double vt;
    private long iteration;

    /**
     * Builds a simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    protected AbstractNBodySimulation(final List<? extends Body> bodies, final double dt) {
        if (dt <= 0) {
            throw new IllegalArgumentException("The time step must be positive: " + dt);
        }
        this.bodies = List.copyOf(bodies);
        this.dt = dt;
    }

    @Override
    public final void step() {
        computeStep();
        vt += dt;
        iteration++;
    }

    /**
     * Executes the two phases of a step on every body, using {@link #updateVelocity(Body)}
     * and {@link #updatePosition(Body)}.
     */
    protected abstract void computeStep();

    /**
     * First phase of a step on a body: updates its velocity according to the total force
     * exerted by the other bodies.
     *
     * @param b the body to update
     */
    protected final void updateVelocity(final Body b) {
        final Vector2D acc = computeTotalForceOn(b).scalarMul(1.0 / b.getMass());
        b.updateVelocity(acc, dt);
    }

    /**
     * Second phase of a step on a body: updates its position according to its velocity.
     *
     * @param b the body to update
     */
    protected final void updatePosition(final Body b) {
        b.updatePos(dt);
    }

    @Override
    public final List<Body> getBodies() {
        return bodies;
    }

    @Override
    public final int getNumBodies() {
        return bodies.size();
    }

    @Override
    public final double getVirtualTime() {
        return vt;
    }

    @Override
    public final long getIteration() {
        return iteration;
    }

    /**
     * Returns the virtual time elapsed at each step.
     *
     * @return the time step
     */
    public final double getTimeStep() {
        return dt;
    }

    private Vector2D computeTotalForceOn(final Body b) {
        Vector2D totalForce = Vector2D.ZERO;
        for (final Body other : bodies) {
            if (!b.equals(other)) {
                try {
                    totalForce = totalForce.sum(b.computeGForceBy(other));
                } catch (final InfiniteForceException ignored) {
                    // bodies in the same position: the force is ignored, as in the reference version
                }
            }
        }
        return totalForce;
    }
}
