package it.unibo.sisco.model.api.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class P2dTest {

    private static final double DELTA = 1e-12;

    @Test
    void sumTranslatesThePoint() {
        final P2d p = new P2d(1, 2).sum(new V2d(0.5, -3));
        assertEquals(1.5, p.x(), DELTA);
        assertEquals(-1, p.y(), DELTA);
    }

    @Test
    void distanceFromIsEuclidean() {
        assertEquals(5, new P2d(0, 0).distanceFrom(new P2d(3, 4)), DELTA);
    }

    @Test
    void distanceFromIsSymmetric() {
        final P2d a = new P2d(-1, 2);
        final P2d b = new P2d(3, -0.5);
        assertEquals(a.distanceFrom(b), b.distanceFrom(a), DELTA);
    }

    @Test
    void distanceFromItselfIsZero() {
        final P2d p = new P2d(0.3, -0.7);
        assertEquals(0, p.distanceFrom(p), DELTA);
    }
}
