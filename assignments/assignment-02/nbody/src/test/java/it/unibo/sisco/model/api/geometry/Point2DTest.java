package it.unibo.sisco.model.api.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.unibo.sisco.model.impl.geometry.Point2D;
import it.unibo.sisco.model.impl.geometry.Vector2D;

class Point2DTest {

    private static final double DELTA = 1e-12;

    @Test
    void sumTranslatesThePoint() {
        final Point2D p = new Point2D(1, 2).sum(new Vector2D(0.5, -3));
        assertEquals(1.5, p.x(), DELTA);
        assertEquals(-1, p.y(), DELTA);
    }

    @Test
    void distanceFromIsEuclidean() {
        assertEquals(5, new Point2D(0, 0).distanceFrom(new Point2D(3, 4)), DELTA);
    }

    @Test
    void distanceFromIsSymmetric() {
        final Point2D a = new Point2D(-1, 2);
        final Point2D b = new Point2D(3, -0.5);
        assertEquals(a.distanceFrom(b), b.distanceFrom(a), DELTA);
    }

    @Test
    void distanceFromItselfIsZero() {
        final Point2D p = new Point2D(0.3, -0.7);
        assertEquals(0, p.distanceFrom(p), DELTA);
    }
}
