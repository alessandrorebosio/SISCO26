package it.unibo.sisco.model.impl.body;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.geometry.Point2D;
import it.unibo.sisco.model.api.geometry.Vector2D;
import it.unibo.sisco.model.exceptions.InfiniteForceException;
import it.unibo.sisco.model.exceptions.NullVectorException;

import java.util.List;

/**
 * {@link Body} modelled as a point mass, attracted by the other bodies with
 * Newtonian gravity and a scaled gravitational constant {@code G = 0.001}.
 *
 * <p>
 * Two bodies are equal if they have the same identifier. The class is not
 * thread-safe.
 */
public final class PointMassBody implements Body {

    private static final double G_CONST_SCALED = 0.001;

    private final int id;
    private final double mass;
    private final double radius;
    private Point2D pos;
    private Vector2D vel;

    /**
     * Builds a new body.
     *
     * @param id     the identifier of the body
     * @param pos    the initial position
     * @param vel    the initial velocity
     * @param radius the radius
     * @param mass   the mass
     */
    public PointMassBody(final int id, final Point2D pos, final Vector2D vel, final double radius, final double mass) {
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.radius = radius;
        this.mass = mass;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Point2D getPos() {
        return this.pos;
    }

    @Override
    public Vector2D getVel() {
        return this.vel;
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public double getDistanceFrom(final Body b) {
        return this.pos.distanceFrom(b.getPos());
    }

    @Override
    public Vector2D computeGForceBy(final Body b) throws InfiniteForceException {
        final double dist = getDistanceFrom(b);
        if (dist > 0) {
            try {
                return Vector2D.between(this.pos, b.getPos())
                        .normalize()
                        .scalarMul(b.getMass() * G_CONST_SCALED / (dist * dist));
            } catch (final NullVectorException e) {
                throw new InfiniteForceException(e);
            }
        }
        throw new InfiniteForceException();
    }

    /*
     * Same formula as computeGForceBy, F = G * m * d / |d|^3, written with
     * primitive values: it runs once for every pair of bodies at each step, so it
     * computes one square root and allocates no intermediate vector.
     */
    @Override
    public Vector2D computeTotalGForceBy(final List<? extends Body> bodies) {
        double fx = 0;
        double fy = 0;
        for (final Body other : bodies) {
            final double dx = other.getPos().x() - this.pos.x();
            final double dy = other.getPos().y() - this.pos.y();
            final double dist2 = dx * dx + dy * dy;
            // dist2 == 0 for this body and for bodies in the same position:
            // the force is ignored, as in the reference version
            if (dist2 > 0) {
                final double k = other.getMass() * G_CONST_SCALED / (dist2 * Math.sqrt(dist2));
                fx += dx * k;
                fy += dy * k;
            }
        }
        return new Vector2D(fx, fy);
    }

    @Override
    public void updateVelocity(final Vector2D acc, final double dt) {
        this.vel = this.vel.sum(acc.scalarMul(dt));
    }

    @Override
    public void updatePos(final double dt) {
        this.pos = this.pos.sum(this.vel.scalarMul(dt));
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Body other && other.getId() == this.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    @Override
    public String toString() {
        return "Body[id=" + this.id + ", pos=" + this.pos + ", vel=" + this.vel + "]";
    }
}
