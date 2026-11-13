package it.unibo.sisco.model.impl.geometry;

/**
 * An immutable point in a 2D space. Being immutable, it can be shared among threads
 * without synchronisation.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 */
public record Point2D(double x, double y) {

    /**
     * Translates this point by a vector.
     *
     * @param v the translation vector
     * @return a new point, translated by {@code v}
     */
    public Point2D sum(final Vector2D v) {
        return new Point2D(x + v.x(), y + v.y());
    }

    /**
     * Computes the euclidean distance from another point.
     *
     * @param other the other point
     * @return the distance between the two points
     */
    public double distanceFrom(final Point2D other) {
        final double dx = x - other.x();
        final double dy = y - other.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
