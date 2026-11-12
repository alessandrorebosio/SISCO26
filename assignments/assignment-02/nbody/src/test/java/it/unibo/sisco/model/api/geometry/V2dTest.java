package it.unibo.sisco.model.api.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class V2dTest {

    private static final double DELTA = 1e-12;

    @Test
    void betweenGoesFromTheFirstToTheSecondPoint() {
        final V2d v = V2d.between(new P2d(1, 1), new P2d(4, -1));
        assertEquals(new V2d(3, -2), v);
    }

    @Test
    void sumAddsTheComponents() {
        assertEquals(new V2d(4, 1), new V2d(1, 2).sum(new V2d(3, -1)));
    }

    @Test
    void scalarMulScalesTheComponents() {
        assertEquals(new V2d(-2, 6), new V2d(1, -3).scalarMul(-2));
    }

    @Test
    void moduleIsTheEuclideanLength() {
        assertEquals(5, new V2d(-3, 4).module(), DELTA);
        assertEquals(0, V2d.ZERO.module(), DELTA);
    }

    @Test
    void normalizeKeepsTheDirectionWithUnitLength() throws NullVectorException {
        final V2d unit = new V2d(3, 4).normalize();
        assertEquals(1, unit.module(), DELTA);
        assertEquals(0.6, unit.x(), DELTA);
        assertEquals(0.8, unit.y(), DELTA);
    }

    @Test
    void normalizeOfTheNullVectorFails() {
        assertThrows(NullVectorException.class, V2d.ZERO::normalize);
    }
}
