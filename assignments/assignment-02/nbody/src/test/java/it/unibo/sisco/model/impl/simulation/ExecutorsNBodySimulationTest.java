package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.impl.body.TestBodies;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import org.junit.jupiter.api.Test;

class ExecutorsNBodySimulationTest extends AbstractNBodySimulationTest {

    private static final int POOL_SIZE = 4;

    @Override
    protected AbstractNBodySimulation newSimulation(final List<? extends Body> bodies, final double dt) {
        return new ExecutorsNBodySimulation(bodies, dt, POOL_SIZE);
    }

    @Test
    void nonPositivePoolSizeIsRejected() {
        final List<Body> bodies = TestBodies.twoBodies();
        assertThrows(IllegalArgumentException.class, () -> new ExecutorsNBodySimulation(bodies, DT, 0));
    }

    @Test
    void anySizeOfThePoolGivesTheSameResult() {
        final NBodySimulation one = track(new ExecutorsNBodySimulation(TestBodies.threeBodies(), DT, 1));
        final NBodySimulation many = track(new ExecutorsNBodySimulation(TestBodies.threeBodies(), DT, POOL_SIZE));
        for (int i = 0; i < STEPS; i++) {
            one.step();
            many.step();
        }
        for (int i = 0; i < one.getNumBodies(); i++) {
            assertEquals(one.getBodies().get(i).getPos(), many.getBodies().get(i).getPos());
        }
    }

    @Test
    void stepAfterCloseFails() {
        final NBodySimulation sim = create(TestBodies.threeBodies(), DT);
        sim.close();
        assertThrows(RejectedExecutionException.class, sim::step);
    }
}
