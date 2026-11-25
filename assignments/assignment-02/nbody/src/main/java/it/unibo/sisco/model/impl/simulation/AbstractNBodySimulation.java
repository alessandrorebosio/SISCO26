package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;

import java.util.List;

/**
 * Skeleton of an {@link NBodySimulation}: it holds the bodies and the virtual
 * time, and provides the per-body operations of a step.
 *
 * <p>
 * A step has two phases, and subclasses decide how to execute them
 * (sequentially, with threads, with tasks, ...):
 *
 * <ol>
 * <li>{@link #updateVelocity(Body)} on every body: it reads the positions of
 * all the bodies and writes only the velocity of its own body;</li>
 * <li>{@link #updatePosition(Body)} on every body: it reads and writes only its
 * own body.</li>
 * </ol>
 *
 * <p>
 * Within a phase, the operations on different bodies are independent of each
 * other, while the second phase must start only after the first one is
 * complete on every body.
 */
public abstract class AbstractNBodySimulation implements NBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;

    private final List<Body> bodies;
    private final double dt;
    private long iteration;
    private double vt;

    /**
     * Builds a simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt     the virtual time elapsed at each step
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
        this.vt += this.dt;
        this.iteration++;
    }

    /**
     * Executes the two phases of a step on every body, using
     * {@link #updateVelocity(Body)} and {@link #updatePosition(Body)}.
     */
    protected abstract void computeStep();

    /**
     * First phase of a step on a body: updates its velocity according to the
     * total force exerted by the other bodies.
     *
     * @param b the body to update
     */
    protected final void updateVelocity(final Body b) {
        b.updateVelocity(b.computeTotalGForceBy(this.bodies).scalarMul(1.0 / b.getMass()), this.dt);
    }

    /**
     * Second phase of a step on a body: updates its position according to its
     * velocity.
     *
     * @param b the body to update
     */
    protected final void updatePosition(final Body b) {
        b.updatePos(this.dt);
    }

    @Override
    public final List<Body> getBodies() {
        return this.bodies;
    }

    @Override
    public final int getNumBodies() {
        return this.bodies.size();
    }

    @Override
    public final double getVirtualTime() {
        return this.vt;
    }

    @Override
    public final long getIteration() {
        return this.iteration;
    }

    /**
     * Returns the virtual time elapsed at each step.
     *
     * @return the time step
     */
    public final double getTimeStep() {
        return this.dt;
    }
}
