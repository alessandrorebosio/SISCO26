package it.unibo.sisco.model.api.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import it.unibo.sisco.model.exceptions.NullVectorException;
import it.unibo.sisco.model.impl.geometry.Point2D;
import it.unibo.sisco.model.impl.geometry.Vector2D;

class Vector2DTest {

    private static final double DELTA = 1e-12;

    @Test
    void betweenGoesFromTheFirstToTheSecondPoint() {
        final Vector2D v = Vector2D.between(new Point2D(1, 1), new Point2D(4, -1));
        assertEquals(new Vector2D(3, -2), v);
    }

    @Test
    void sumAddsTheComponents() {
        assertEquals(new Vector2D(4, 1), new Vector2D(1, 2).sum(new Vector2D(3, -1)));
    }

    @Test
    void scalarMulScalesTheComponents() {
        assertEquals(new Vector2D(-2, 6), new Vector2D(1, -3).scalarMul(-2));
    }

    @Test
    void moduleIsTheEuclideanLength() {
        assertEquals(5, new Vector2D(-3, 4).module(), DELTA);
        assertEquals(0, Vector2D.ZERO.module(), DELTA);
    }

    @Test
    void normalizeKeepsTheDirectionWithUnitLength() throws NullVectorException {
        final Vector2D unit = new Vector2D(3, 4).normalize();
        assertEquals(1, unit.module(), DELTA);
        assertEquals(0.6, unit.x(), DELTA);
        assertEquals(0.8, unit.y(), DELTA);
    }

    @Test
    void normalizeOfTheNullVectorFails() {
        assertThrows(NullVectorException.class, Vector2D.ZERO::normalize);
    }
}
