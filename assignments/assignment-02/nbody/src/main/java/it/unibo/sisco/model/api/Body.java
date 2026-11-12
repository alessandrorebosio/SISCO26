package it.unibo.sisco.model.api;

import it.unibo.sisco.model.api.geometry.P2d;
import it.unibo.sisco.model.api.geometry.V2d;

/**
 * A body of the simulation, with a position, a velocity, a mass and a radius.
 */
public interface Body {

    /**
     * Returns the identifier of the body.
     *
     * @return the identifier of the body
     */
    int getId();

    /**
     * Returns the current position of the body.
     *
     * @return the current position
     */
    P2d getPos();

    /**
     * Returns the current velocity of the body.
     *
     * @return the current velocity
     */
    V2d getVel();

    /**
     * Returns the mass of the body.
     *
     * @return the mass
     */
    double getMass();

    /**
     * Returns the radius of the body.
     *
     * @return the radius
     */
    double getRadius();

    /**
     * Computes the distance from another body.
     *
     * @param b the other body
     * @return the distance between the two bodies
     */
    double getDistanceFrom(Body b);

    /**
     * Computes the gravitational force exerted on this body by another body.
     *
     * @param b the body exerting the force
     * @return the force exerted by {@code b}
     * @throws InfiniteForceException if the two bodies are in the same position
     */
    V2d computeGForceBy(Body b) throws InfiniteForceException;

    /**
     * Updates the velocity, given the instant acceleration.
     *
     * @param acc the instant acceleration
     * @param dt the virtual time elapsed
     */
    void updateVelocity(V2d acc, double dt);

    /**
     * Updates the position, according to the current velocity.
     *
     * @param dt the virtual time elapsed
     */
    void updatePos(double dt);
}
