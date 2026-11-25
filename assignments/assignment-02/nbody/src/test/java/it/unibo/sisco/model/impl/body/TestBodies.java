package it.unibo.sisco.model.impl.body;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.impl.geometry.Point2D;
import it.unibo.sisco.model.impl.geometry.Vector2D;

import java.util.List;

/**
 * Small predefined body sets used by the tests, with identifiers from
 * {@code 0} to {@code size - 1}.
 */
public final class TestBodies {

    private static final double RADIUS = 0.1;

    private TestBodies() {
    }

    /**
     * Builds a set of two bodies with the same mass, initially still.
     *
     * @return the body set
     */
    public static List<Body> twoBodies() {
        return List.of(
                new PointMassBody(0, new Point2D(-0.3, 0), Vector2D.ZERO, RADIUS, 1),
                new PointMassBody(1, new Point2D(0.3, 0), Vector2D.ZERO, RADIUS, 1));
    }

    /**
     * Builds a set of three bodies: a heavy one in the centre and two lighter ones
     * orbiting it.
     *
     * @return the body set
     */
    public static List<Body> threeBodies() {
        return List.of(
                new PointMassBody(0, new Point2D(0, 0), Vector2D.ZERO, RADIUS, 20),
                new PointMassBody(1, new Point2D(0.3, 0), new Vector2D(0, 0.25), RADIUS, 1),
                new PointMassBody(2, new Point2D(-0.5, 0), new Vector2D(0, -0.15), RADIUS, 1));
    }
}
