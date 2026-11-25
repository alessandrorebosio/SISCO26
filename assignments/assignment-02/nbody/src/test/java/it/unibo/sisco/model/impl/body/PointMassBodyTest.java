package it.unibo.sisco.model.impl.body;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.geometry.Point2D;
import it.unibo.sisco.model.api.geometry.Vector2D;
import it.unibo.sisco.model.exceptions.InfiniteForceException;

import java.util.List;
import org.junit.jupiter.api.Test;

class PointMassBodyTest {

    private static final double DELTA = 1e-12;
    private static final double G = 0.001;

    @Test
    void gettersReturnTheInitialState() {
        final Body b = new PointMassBody(7, new Point2D(1, 2), new Vector2D(3, 4), 0.5, 10);
        assertEquals(7, b.getId());
        assertEquals(new Point2D(1, 2), b.getPos());
        assertEquals(new Vector2D(3, 4), b.getVel());
        assertEquals(0.5, b.getRadius(), DELTA);
        assertEquals(10, b.getMass(), DELTA);
    }

    @Test
    void updateVelocityAddsAccelerationTimesDt() {
        final Body b = new PointMassBody(0, new Point2D(0, 0), new Vector2D(1, 1), 0.1, 1);
        b.updateVelocity(new Vector2D(2, -4), 0.5);
        assertEquals(2, b.getVel().x(), DELTA);
        assertEquals(-1, b.getVel().y(), DELTA);
        assertEquals(new Point2D(0, 0), b.getPos());
    }

    @Test
    void updatePosAddsVelocityTimesDt() {
        final Body b = new PointMassBody(0, new Point2D(1, 1), new Vector2D(2, -4), 0.1, 1);
        b.updatePos(0.5);
        assertEquals(2, b.getPos().x(), DELTA);
        assertEquals(-1, b.getPos().y(), DELTA);
        assertEquals(new Vector2D(2, -4), b.getVel());
    }

    @Test
    void distanceFromIsTheDistanceBetweenThePositions() {
        final Body a = new PointMassBody(0, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final Body b = new PointMassBody(1, new Point2D(3, 4), Vector2D.ZERO, 0.1, 1);
        assertEquals(5, a.getDistanceFrom(b), DELTA);
    }

    @Test
    void gravitationalForcePointsTowardsTheOtherBody() throws InfiniteForceException {
        final Body a = new PointMassBody(0, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final Body b = new PointMassBody(1, new Point2D(2, 0), Vector2D.ZERO, 0.1, 4);
        final Vector2D force = a.computeGForceBy(b);
        /* magnitude: mass of the other body * G / distance^2 */
        assertEquals(4 * G / 4, force.x(), DELTA);
        assertEquals(0, force.y(), DELTA);
    }

    @Test
    void gravitationalForceDecreasesWithTheSquareOfTheDistance() throws InfiniteForceException {
        final Body a = new PointMassBody(0, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final Body near = new PointMassBody(1, new Point2D(0, 1), Vector2D.ZERO, 0.1, 1);
        final Body far = new PointMassBody(2, new Point2D(0, 2), Vector2D.ZERO, 0.1, 1);
        assertEquals(4, a.computeGForceBy(near).module() / a.computeGForceBy(far).module(), DELTA);
    }

    @Test
    void forcesBetweenTwoBodiesHaveOppositeDirections() throws InfiniteForceException {
        final Body a = new PointMassBody(0, new Point2D(-0.3, 0.1), Vector2D.ZERO, 0.1, 2);
        final Body b = new PointMassBody(1, new Point2D(0.5, -0.2), Vector2D.ZERO, 0.1, 2);
        final Vector2D onA = a.computeGForceBy(b);
        final Vector2D onB = b.computeGForceBy(a);
        assertEquals(-onA.x(), onB.x(), DELTA);
        assertEquals(-onA.y(), onB.y(), DELTA);
    }

    @Test
    void forceBetweenBodiesInTheSamePositionIsInfinite() {
        final Body a = new PointMassBody(0, new Point2D(1, 1), Vector2D.ZERO, 0.1, 1);
        final Body b = new PointMassBody(1, new Point2D(1, 1), Vector2D.ZERO, 0.1, 1);
        assertThrows(InfiniteForceException.class, () -> a.computeGForceBy(b));
    }

    @Test
    void bodiesAreEqualIfTheyHaveTheSameId() {
        final Body a = new PointMassBody(3, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final Body sameId = new PointMassBody(3, new Point2D(5, 5), new Vector2D(1, 1), 1, 9);
        final Body otherId = new PointMassBody(4, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        assertEquals(a, sameId);
        assertEquals(a.hashCode(), sameId.hashCode());
        assertNotEquals(a, otherId);
    }

    @Test
    void descriptionContainsTheId() {
        assertTrue(new PointMassBody(42, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1).toString().contains("id=42"));
    }

    @Test
    void totalForceIsTheSumOfTheSingleForcesAndIgnoresCoincidentBodies() throws InfiniteForceException {
        final Body a = new PointMassBody(0, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final Body b = new PointMassBody(1, new Point2D(2, 0), Vector2D.ZERO, 0.1, 4);
        final Body c = new PointMassBody(2, new Point2D(-1, 3), Vector2D.ZERO, 0.1, 2);
        final Body sameAsA = new PointMassBody(3, new Point2D(0, 0), Vector2D.ZERO, 0.1, 7);
        final Vector2D expected = a.computeGForceBy(b).sum(a.computeGForceBy(c));
        final Vector2D total = a.computeTotalGForceBy(List.of(a, b, c, sameAsA));
        assertEquals(expected.x(), total.x(), DELTA);
        assertEquals(expected.y(), total.y(), DELTA);
    }
}
