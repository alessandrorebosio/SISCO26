package it.unibo.sisco.model.impl.body;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.exceptions.InfiniteForceException;
import it.unibo.sisco.model.exceptions.NullVectorException;
import it.unibo.sisco.model.impl.geometry.Point2D;
import it.unibo.sisco.model.impl.geometry.Vector2D;

/**
 * Default implementation of {@link Body}, with a scaled gravitational constant
 * {@code G = 0.001}.
 *
 * <p>Two bodies are equal if they have the same identifier. The class is not thread-safe.
 */
public final class BodyImpl implements Body {

    private static final double G_CONST_SCALED = 0.001;

    private final int id;
    private final double mass;
    private final double radius;
    private Point2D pos;
    private Vector2D vel;

    /**
     * Builds a new body.
     *
     * @param id the identifier of the body
     * @param pos the initial position
     * @param vel the initial velocity
     * @param radius the radius
     * @param mass the mass
     */
    public BodyImpl(final int id, final Point2D pos, final Vector2D vel, final double radius, final double mass) {
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.radius = radius;
        this.mass = mass;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Point2D getPos() {
        return pos;
    }

    @Override
    public Vector2D getVel() {
        return vel;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getDistanceFrom(final Body b) {
        return pos.distanceFrom(b.getPos());
    }

    @Override
    public Vector2D computeGForceBy(final Body b) throws InfiniteForceException {
        final double dist = getDistanceFrom(b);
        if (dist > 0) {
            try {
                return Vector2D.between(pos, b.getPos())
                    .normalize()
                    .scalarMul(b.getMass() * G_CONST_SCALED / (dist * dist));
            } catch (final NullVectorException e) {
                throw new InfiniteForceException(e);
            }
        }
        throw new InfiniteForceException();
    }

    @Override
    public void updateVelocity(final Vector2D acc, final double dt) {
        vel = vel.sum(acc.scalarMul(dt));
    }

    @Override
    public void updatePos(final double dt) {
        pos = pos.sum(vel.scalarMul(dt));
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Body other && other.getId() == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Body[id=" + id + ", pos=" + pos + ", vel=" + vel + "]";
    }
}
