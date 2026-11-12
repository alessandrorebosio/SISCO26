package it.unibo.sisco.model.impl.body;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.geometry.P2d;
import it.unibo.sisco.model.api.geometry.V2d;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory of body sets to initialise a simulation. Every set is an unmodifiable list of new
 * bodies, with identifiers from {@code 0} to {@code size - 1}.
 */
public final class BodySets {

    // CHECKSTYLE: MagicNumber OFF
    private static final double RANDOM_RADIUS = 0.01;
    private static final double MAX_SPEED = 0.1;
    private static final double MIN_MASS = 1;
    private static final double MASS_RANGE = 9;
    private static final double TEST_RADIUS = 0.1;
    // CHECKSTYLE: MagicNumber ON

    private BodySets() {
    }

    /**
     * Builds a set of two bodies with the same mass, initially still.
     *
     * @return the body set
     */
    public static List<Body> twoBodies() {
        // CHECKSTYLE: MagicNumber OFF
        return List.of(
            new BodyImpl(0, new P2d(-0.3, 0), V2d.ZERO, TEST_RADIUS, 1),
            new BodyImpl(1, new P2d(0.3, 0), V2d.ZERO, TEST_RADIUS, 1)
        );
        // CHECKSTYLE: MagicNumber ON
    }

    /**
     * Builds a set of three bodies: a heavy one in the centre and two lighter ones orbiting it.
     *
     * @return the body set
     */
    public static List<Body> threeBodies() {
        // CHECKSTYLE: MagicNumber OFF
        return List.of(
            new BodyImpl(0, new P2d(0, 0), V2d.ZERO, TEST_RADIUS, 20),
            new BodyImpl(1, new P2d(0.3, 0), new V2d(0, 0.25), TEST_RADIUS, 1),
            new BodyImpl(2, new P2d(-0.5, 0), new V2d(0, -0.15), TEST_RADIUS, 1)
        );
        // CHECKSTYLE: MagicNumber ON
    }

    /**
     * Builds a set of bodies with random position, velocity and mass, seeded with the current
     * time, so the set is not reproducible.
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
     * <p>Positions are in {@code [-1, 1)} on both axes, velocity components in
     * {@code [-0.1, 0.1)}, masses in {@code [1, 10)} and every radius is {@code 0.01}.
     *
     * @param nBodies the number of bodies
     * @param seed the seed of the random generator, to get reproducible sets
     * @return the body set
     */
    public static List<Body> random(final int nBodies, final long seed) {
        final Random rand = new Random(seed);
        final List<Body> bodies = new ArrayList<>(nBodies);
        for (int i = 0; i < nBodies; i++) {
            final double x = -1.0 + 2.0 * rand.nextDouble();
            final double y = -1.0 + 2.0 * rand.nextDouble();
            final double vx = -MAX_SPEED + 2 * MAX_SPEED * rand.nextDouble();
            final double vy = -MAX_SPEED + 2 * MAX_SPEED * rand.nextDouble();
            final double m = MIN_MASS + rand.nextDouble() * MASS_RANGE;
            bodies.add(new BodyImpl(i, new P2d(x, y), new V2d(vx, vy), RANDOM_RADIUS, m));
        }
        return List.copyOf(bodies);
    }
}
