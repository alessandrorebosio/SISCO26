package it.unibo.sisco.model.impl.body;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.geometry.Point2D;
import it.unibo.sisco.model.api.geometry.Vector2D;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Factory of body sets to initialise a simulation. Every set is an unmodifiable
 * list of new bodies, with identifiers from {@code 0} to {@code size - 1}.
 */
public final class BodySets {

    private static final double RANDOM_RADIUS = 0.01;
    private static final double MAX_SPEED = 0.1;
    private static final double MIN_MASS = 1;
    private static final double MASS_RANGE = 9;

    private BodySets() {
    }

    /**
     * Builds a set of bodies with random position, velocity and mass, seeded with
     * the current time, so the set is not reproducible.
     *
     * @param nBodies the number of bodies
     * @return the body set
     * @see #random(int, long)
     */
    public static List<Body> random(final int nBodies) {
        return random(nBodies, System.currentTimeMillis());
    }

    /**
     * Builds a set of bodies with random position, velocity and mass.
     *
     * <p>
     * Positions are in {@code [-1, 1)} on both axes, velocity components in
     * {@code [-0.1, 0.1)}, masses in {@code [1, 10)} and every radius is
     * {@code 0.01}.
     *
     * @param nBodies the number of bodies
     * @param seed    the seed of the random generator, to get reproducible sets
     * @return the body set
     */
    public static List<Body> random(final int nBodies, final long seed) {
        final Random rand = new Random(seed);
        return IntStream.range(0, nBodies)
                .mapToObj(i -> randomBody(i, rand))
                .toList();
    }

    private static Body randomBody(final int id, final Random rand) {
        final double x = -1.0 + 2.0 * rand.nextDouble();
        final double y = -1.0 + 2.0 * rand.nextDouble();
        final double vx = -MAX_SPEED + 2 * MAX_SPEED * rand.nextDouble();
        final double vy = -MAX_SPEED + 2 * MAX_SPEED * rand.nextDouble();
        final double m = MIN_MASS + rand.nextDouble() * MASS_RANGE;
        return new PointMassBody(id, new Point2D(x, y), new Vector2D(vx, vy), RANDOM_RADIUS, m);
    }
}
