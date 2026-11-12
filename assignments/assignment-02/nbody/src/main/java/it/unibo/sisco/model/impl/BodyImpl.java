package it.unibo.sisco.model.impl;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.InfiniteForceException;
import it.unibo.sisco.model.api.NullVectorException;
import it.unibo.sisco.model.api.P2d;
import it.unibo.sisco.model.api.V2d;

/**
 * Default implementation of {@link Body}. Two bodies are equal if they have the same identifier.
 */
public final class BodyImpl implements Body {

    private static final double G_CONST_SCALED = 0.001;

    private final int id;
    private final double mass;
    private final double radius;
    private P2d pos;
    private V2d vel;

    /**
     * Builds a new body.
     *
     * @param id the identifier of the body
     * @param pos the initial position
     * @param vel the initial velocity
     * @param radius the radius
     * @param mass the mass
     */
    public BodyImpl(final int id, final P2d pos, final V2d vel, final double radius, final double mass) {
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
    public P2d getPos() {
        return pos;
    }

    @Override
    public V2d getVel() {
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
    public V2d computeGForceBy(final Body b) throws InfiniteForceException {
        final double dist = getDistanceFrom(b);
        if (dist > 0) {
            try {
                return V2d.between(pos, b.getPos())
                    .normalize()
                    .scalarMul(b.getMass() * G_CONST_SCALED / (dist * dist));
            } catch (final NullVectorException e) {
                throw new InfiniteForceException(e);
            }
        }
        throw new InfiniteForceException();
    }

    @Override
    public void updateVelocity(final V2d acc, final double dt) {
        vel = vel.sum(acc.scalarMul(dt));
    }

    @Override
    public void updatePos(final double dt) {
        pos = pos.sum(vel.scalarMul(dt));
    }

    @Override
    public void changeVel(final V2d newVel) {
        this.vel = newVel;
    }

    @Override
    public void forceTranslation(final V2d v) {
        pos = pos.sum(v);
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
