package it.unibo.sisco.model.impl.body;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.InfiniteForceException;
import it.unibo.sisco.model.api.geometry.P2d;
import it.unibo.sisco.model.api.geometry.V2d;
import org.junit.jupiter.api.Test;

class BodyImplTest {

    private static final double DELTA = 1e-12;
    private static final double G = 0.001;

    @Test
    void gettersReturnTheInitialState() {
        final Body b = new BodyImpl(7, new P2d(1, 2), new V2d(3, 4), 0.5, 10);
        assertEquals(7, b.getId());
        assertEquals(new P2d(1, 2), b.getPos());
        assertEquals(new V2d(3, 4), b.getVel());
        assertEquals(0.5, b.getRadius(), DELTA);
        assertEquals(10, b.getMass(), DELTA);
    }

    @Test
    void updateVelocityAddsAccelerationTimesDt() {
        final Body b = new BodyImpl(0, new P2d(0, 0), new V2d(1, 1), 0.1, 1);
        b.updateVelocity(new V2d(2, -4), 0.5);
        assertEquals(2, b.getVel().x(), DELTA);
        assertEquals(-1, b.getVel().y(), DELTA);
        assertEquals(new P2d(0, 0), b.getPos());
    }

    @Test
    void updatePosAddsVelocityTimesDt() {
        final Body b = new BodyImpl(0, new P2d(1, 1), new V2d(2, -4), 0.1, 1);
        b.updatePos(0.5);
        assertEquals(2, b.getPos().x(), DELTA);
        assertEquals(-1, b.getPos().y(), DELTA);
        assertEquals(new V2d(2, -4), b.getVel());
    }

    @Test
    void distanceFromIsTheDistanceBetweenThePositions() {
        final Body a = new BodyImpl(0, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        final Body b = new BodyImpl(1, new P2d(3, 4), V2d.ZERO, 0.1, 1);
        assertEquals(5, a.getDistanceFrom(b), DELTA);
    }

    @Test
    void gravitationalForcePointsTowardsTheOtherBody() throws InfiniteForceException {
        final Body a = new BodyImpl(0, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        final Body b = new BodyImpl(1, new P2d(2, 0), V2d.ZERO, 0.1, 4);
        final V2d force = a.computeGForceBy(b);
        /* magnitude: mass of the other body * G / distance^2 */
        assertEquals(4 * G / 4, force.x(), DELTA);
        assertEquals(0, force.y(), DELTA);
    }

    @Test
    void gravitationalForceDecreasesWithTheSquareOfTheDistance() throws InfiniteForceException {
        final Body a = new BodyImpl(0, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        final Body near = new BodyImpl(1, new P2d(0, 1), V2d.ZERO, 0.1, 1);
        final Body far = new BodyImpl(2, new P2d(0, 2), V2d.ZERO, 0.1, 1);
        assertEquals(4, a.computeGForceBy(near).module() / a.computeGForceBy(far).module(), DELTA);
    }

    @Test
    void forcesBetweenTwoBodiesHaveOppositeDirections() throws InfiniteForceException {
        final Body a = new BodyImpl(0, new P2d(-0.3, 0.1), V2d.ZERO, 0.1, 2);
        final Body b = new BodyImpl(1, new P2d(0.5, -0.2), V2d.ZERO, 0.1, 2);
        final V2d onA = a.computeGForceBy(b);
        final V2d onB = b.computeGForceBy(a);
        assertEquals(-onA.x(), onB.x(), DELTA);
        assertEquals(-onA.y(), onB.y(), DELTA);
    }

    @Test
    void forceBetweenBodiesInTheSamePositionIsInfinite() {
        final Body a = new BodyImpl(0, new P2d(1, 1), V2d.ZERO, 0.1, 1);
        final Body b = new BodyImpl(1, new P2d(1, 1), V2d.ZERO, 0.1, 1);
        assertThrows(InfiniteForceException.class, () -> a.computeGForceBy(b));
    }

    @Test
    void bodiesAreEqualIfTheyHaveTheSameId() {
        final Body a = new BodyImpl(3, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        final Body sameId = new BodyImpl(3, new P2d(5, 5), new V2d(1, 1), 1, 9);
        final Body otherId = new BodyImpl(4, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        assertEquals(a, sameId);
        assertEquals(a.hashCode(), sameId.hashCode());
        assertNotEquals(a, otherId);
    }

    @Test
    void descriptionContainsTheId() {
        assertTrue(new BodyImpl(42, new P2d(0, 0), V2d.ZERO, 0.1, 1).toString().contains("id=42"));
    }
}
