package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.geometry.P2d;
import it.unibo.sisco.model.api.geometry.V2d;
import it.unibo.sisco.model.impl.body.BodyImpl;
import it.unibo.sisco.model.impl.body.BodySets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SequentialNBodySimulationTest {

    private static final double DELTA = 1e-12;
    private static final double DT = 0.01;
    private static final int STEPS = 100;

    @Test
    void initialStateHasNoStepsAndZeroTime() {
        final NBodySimulation sim = new SequentialNBodySimulation(BodySets.threeBodies());
        assertEquals(3, sim.getNumBodies());
        assertEquals(0, sim.getIteration());
        assertEquals(0, sim.getVirtualTime(), DELTA);
    }

    @Test
    void defaultTimeStepIsUsed() {
        final SequentialNBodySimulation sim = new SequentialNBodySimulation(BodySets.twoBodies());
        assertEquals(SequentialNBodySimulation.DEFAULT_DT, sim.getTimeStep(), DELTA);
    }

    @Test
    void everyStepAdvancesIterationAndVirtualTime() {
        final NBodySimulation sim = new SequentialNBodySimulation(BodySets.threeBodies(), DT);
        for (int i = 0; i < STEPS; i++) {
            sim.step();
        }
        assertEquals(STEPS, sim.getIteration());
        assertEquals(STEPS * DT, sim.getVirtualTime(), 1e-9);
    }

    @Test
    void nonPositiveTimeStepIsRejected() {
        final List<Body> bodies = BodySets.twoBodies();
        assertThrows(IllegalArgumentException.class, () -> new SequentialNBodySimulation(bodies, 0));
        assertThrows(IllegalArgumentException.class, () -> new SequentialNBodySimulation(bodies, -DT));
    }

    @Test
    void bodiesAreCopiedAndUnmodifiable() {
        final List<Body> bodies = new ArrayList<>(BodySets.twoBodies());
        final NBodySimulation sim = new SequentialNBodySimulation(bodies);
        bodies.clear();
        assertEquals(2, sim.getNumBodies());
        assertThrows(UnsupportedOperationException.class, () -> sim.getBodies().clear());
    }

    @Test
    void isolatedBodyMovesInAStraightLine() {
        final Body b = new BodyImpl(0, new P2d(0, 0), new V2d(1, -2), 0.1, 1);
        final NBodySimulation sim = new SequentialNBodySimulation(List.of(b), DT);
        for (int i = 0; i < STEPS; i++) {
            sim.step();
        }
        assertEquals(STEPS * DT, b.getPos().x(), 1e-9);
        assertEquals(-2 * STEPS * DT, b.getPos().y(), 1e-9);
        assertEquals(new V2d(1, -2), b.getVel());
    }

    @Test
    void twoEqualBodiesAttractEachOtherSymmetrically() {
        final List<Body> bodies = BodySets.twoBodies();
        final Body left = bodies.get(0);
        final Body right = bodies.get(1);
        final double initialDistance = left.getDistanceFrom(right);
        final NBodySimulation sim = new SequentialNBodySimulation(bodies, DT);
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
        final Body a = new BodyImpl(0, new P2d(-0.5, 0), new V2d(10, 0), 0.1, 1);
        final Body b = new BodyImpl(1, new P2d(0.5, 0), V2d.ZERO, 0.1, 1);
        final NBodySimulation sim = new SequentialNBodySimulation(List.of(a, b), DT);
        sim.step();
        final double expectedSpeed = 1 * 0.001 / (1.0 * 1.0) * DT;
        assertEquals(-expectedSpeed, b.getVel().x(), DELTA);
    }

    @Test
    void bodiesInTheSamePositionDoNotBreakTheSimulation() {
        final Body a = new BodyImpl(0, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        final Body b = new BodyImpl(1, new P2d(0, 0), V2d.ZERO, 0.1, 1);
        final NBodySimulation sim = new SequentialNBodySimulation(List.of(a, b), DT);
        sim.step();
        assertEquals(V2d.ZERO, a.getVel());
        assertEquals(V2d.ZERO, b.getVel());
    }
}
