package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.BodyState;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulationState;
import it.unibo.sisco.model.api.geometry.Point2D;
import it.unibo.sisco.model.api.geometry.Vector2D;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.body.PointMassBody;
import it.unibo.sisco.model.impl.body.TestBodies;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that every implementation of the simulation must pass: each subclass
 * only says how to build its implementation.
 */
abstract class AbstractNBodySimulationTest {

    /** Tolerance of the comparisons between computed values. */
    protected static final double DELTA = 1e-12;

    /** Time step used by the tests, larger than the default one. */
    protected static final double DT = 0.01;

    /** Number of steps run by the tests that let the bodies move. */
    protected static final int STEPS = 100;

    private static final int RANDOM_BODIES = 200;
    private static final long SEED = 42;

    private final List<NBodySimulation> created = new ArrayList<>();

    /**
     * Builds the simulation under test with the given time step.
     *
     * @param bodies the bodies of the simulation
     * @param dt     the time step
     * @return the simulation
     */
    protected abstract AbstractNBodySimulation newSimulation(List<? extends Body> bodies, double dt);

    /**
     * Registers a simulation, which is closed at the end of the test.
     *
     * @param <S>        the type of the simulation
     * @param simulation the simulation to close
     * @return the same simulation
     */
    protected final <S extends NBodySimulation> S track(final S simulation) {
        this.created.add(simulation);
        return simulation;
    }

    /** Closes every simulation built by the test. */
    @AfterEach
    void closeSimulations() {
        this.created.forEach(NBodySimulation::close);
    }

    private AbstractNBodySimulation create(final List<? extends Body> bodies) {
        return create(bodies, AbstractNBodySimulation.DEFAULT_DT);
    }

    /**
     * Builds the simulation under test with the given time step; it is closed at
     * the end of the test.
     *
     * @param bodies the bodies of the simulation
     * @param dt     the time step
     * @return the simulation
     */
    protected final AbstractNBodySimulation create(final List<? extends Body> bodies, final double dt) {
        return track(newSimulation(bodies, dt));
    }

    @Test
    void initialStateHasNoStepsAndZeroTime() {
        final NBodySimulation sim = create(TestBodies.threeBodies());
        assertEquals(3, sim.getNumBodies());
        assertEquals(0, sim.getIteration());
        assertEquals(0, sim.getVirtualTime(), DELTA);
    }

    @Test
    void defaultTimeStepIsUsed() {
        assertEquals(AbstractNBodySimulation.DEFAULT_DT, create(TestBodies.twoBodies()).getTimeStep(), DELTA);
    }

    @Test
    void everyStepAdvancesIterationAndVirtualTime() {
        final NBodySimulation sim = create(TestBodies.threeBodies(), DT);
        for (int i = 0; i < STEPS; i++) {
            sim.step();
        }
        assertEquals(STEPS, sim.getIteration());
        assertEquals(STEPS * DT, sim.getVirtualTime(), 1e-9);
    }

    @Test
    void nonPositiveTimeStepIsRejected() {
        final List<Body> bodies = TestBodies.twoBodies();
        assertThrows(IllegalArgumentException.class, () -> create(bodies, 0));
        assertThrows(IllegalArgumentException.class, () -> create(bodies, -DT));
    }

    @Test
    void bodiesAreCopiedAndUnmodifiable() {
        final List<Body> bodies = new ArrayList<>(TestBodies.twoBodies());
        final NBodySimulation sim = create(bodies);
        bodies.clear();
        assertEquals(2, sim.getNumBodies());
        assertThrows(UnsupportedOperationException.class, () -> sim.getBodies().clear());
    }

    @Test
    void stateIsASnapshotOfTheSimulation() {
        final NBodySimulation sim = create(TestBodies.threeBodies(), DT);
        sim.step();
        final SimulationState state = sim.getState();
        final List<BodyState> before = state.bodies();
        assertEquals(1, state.iteration());
        assertEquals(DT, state.vt(), DELTA);
        assertEquals(sim.getNumBodies(), before.size());
        for (int i = 0; i < before.size(); i++) {
            final Body b = sim.getBodies().get(i);
            assertEquals(new BodyState(b.getPos().x(), b.getPos().y(), b.getRadius()), before.get(i));
        }
        final List<BodyState> copy = List.copyOf(before);
        sim.step();
        assertEquals(copy, before);
        assertThrows(UnsupportedOperationException.class, before::clear);
    }

    @Test
    void isolatedBodyMovesInAStraightLine() {
        final Body b = new PointMassBody(0, new Point2D(0, 0), new Vector2D(1, -2), 0.1, 1);
        final NBodySimulation sim = create(List.of(b), DT);
        for (int i = 0; i < STEPS; i++) {
            sim.step();
        }
        assertEquals(STEPS * DT, b.getPos().x(), 1e-9);
        assertEquals(-2 * STEPS * DT, b.getPos().y(), 1e-9);
        assertEquals(new Vector2D(1, -2), b.getVel());
    }

    @Test
    void twoEqualBodiesAttractEachOtherSymmetrically() {
        final List<Body> bodies = TestBodies.twoBodies();
        final Body left = bodies.get(0);
        final Body right = bodies.get(1);
        final double initialDistance = left.getDistanceFrom(right);
        final NBodySimulation sim = create(bodies, DT);
        for (int i = 0; i < STEPS; i++) {
            sim.step();
        }
        assertTrue(left.getDistanceFrom(right) < initialDistance);
        assertTrue(left.getVel().x() > 0);
        assertTrue(right.getVel().x() < 0);
        assertEquals(-left.getPos().x(), right.getPos().x(), DELTA);
        assertEquals(0, left.getPos().y(), DELTA);
        assertEquals(0, right.getPos().y(), DELTA);
    }

    @Test
    void velocitiesAreUpdatedBeforeAnyPositionChanges() {
        /* the force on the second body must be computed from the positions before the step */
        final Body a = new PointMassBody(0, new Point2D(-0.5, 0), new Vector2D(10, 0), 0.1, 1);
        final Body b = new PointMassBody(1, new Point2D(0.5, 0), Vector2D.ZERO, 0.1, 1);
        final NBodySimulation sim = create(List.of(a, b), DT);
        sim.step();
        final double expectedSpeed = 1 * 0.001 / (1.0 * 1.0) * DT;
        assertEquals(-expectedSpeed, b.getVel().x(), DELTA);
    }

    @Test
    void bodiesInTheSamePositionDoNotBreakTheSimulation() {
        final Body a = new PointMassBody(0, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final Body b = new PointMassBody(1, new Point2D(0, 0), Vector2D.ZERO, 0.1, 1);
        final NBodySimulation sim = create(List.of(a, b), DT);
        sim.step();
        assertEquals(Vector2D.ZERO, a.getVel());
        assertEquals(Vector2D.ZERO, b.getVel());
    }

    @Test
    void closedSimulationCanBeClosedAgainAndRead() {
        final NBodySimulation sim = create(TestBodies.threeBodies(), DT);
        sim.step();
        sim.close();
        sim.close();
        assertEquals(1, sim.getState().iteration());
    }

    @Test
    void resultIsTheSameAsTheSequentialSimulation() {
        /* every body is computed with the same operations in the same order, so the results are identical */
        final NBodySimulation expected = track(new SequentialNBodySimulation(BodySets.random(RANDOM_BODIES, SEED), DT));
        final NBodySimulation actual = create(BodySets.random(RANDOM_BODIES, SEED), DT);
        for (int i = 0; i < STEPS; i++) {
            expected.step();
            actual.step();
        }
        for (int i = 0; i < RANDOM_BODIES; i++) {
            assertEquals(expected.getBodies().get(i).getPos(), actual.getBodies().get(i).getPos());
            assertEquals(expected.getBodies().get(i).getVel(), actual.getBodies().get(i).getVel());
        }
    }
}
