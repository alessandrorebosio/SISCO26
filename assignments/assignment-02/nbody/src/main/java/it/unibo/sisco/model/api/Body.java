package it.unibo.sisco.model.api;

import it.unibo.sisco.model.api.geometry.P2d;
import it.unibo.sisco.model.api.geometry.V2d;

/**
 * A body of the simulation: a point mass that moves in a 2D space.
 *
 * <p>Identifier, mass and radius never change, while position and velocity are updated at
 * every step of the simulation. Implementations are not required to be thread-safe: a
 * concurrent simulation must make sure that a body is never written while other threads
 * read or write it.
 */
public interface Body {

    /**
     * Returns the identifier of the body, unique within a simulation.
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
     * <p>The force points from this body towards {@code b} and its module is
     * {@code G * m_b / d^2}, where {@code m_b} is the mass of {@code b}, {@code d} the distance
     * between the two bodies and {@code G} a scaled gravitational constant. Neither body is
     * modified.
     *
     * @param b the body exerting the force
     * @return the force exerted by {@code b}
     * @throws InfiniteForceException if the two bodies are in the same position
     */
    V2d computeGForceBy(Body b) throws InfiniteForceException;

    /**
     * Updates the velocity, given the instant acceleration: {@code vel = vel + acc * dt}.
     * The position does not change.
     *
     * @param acc the instant acceleration
     * @param dt the virtual time elapsed
     */
    void updateVelocity(V2d acc, double dt);

    /**
     * Updates the position, according to the current velocity: {@code pos = pos + vel * dt}.
     * The velocity does not change.
     *
     * @param dt the virtual time elapsed
     */
    void updatePos(double dt);
}
