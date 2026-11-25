package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.impl.body.TestBodies;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExecutorsNBodySimulationTest extends AbstractNBodySimulationTest {

    private static final int POOL_SIZE = 4;

    @Override
    protected AbstractNBodySimulation create(final List<? extends Body> bodies) {
        return new ExecutorsNBodySimulation(bodies);
    }

    @Override
    protected AbstractNBodySimulation create(final List<? extends Body> bodies, final double dt) {
        return new ExecutorsNBodySimulation(bodies, dt);
    }

    @Test
    void nonPositivePoolSizeIsRejected() {
        final List<Body> bodies = TestBodies.twoBodies();
        assertThrows(IllegalArgumentException.class, () -> new ExecutorsNBodySimulation(bodies, DT, 0));
    }

    @Test
    void anySizeOfThePoolGivesTheSameResult() {
        final NBodySimulation one = new ExecutorsNBodySimulation(TestBodies.threeBodies(), DT, 1);
        final NBodySimulation many = new ExecutorsNBodySimulation(TestBodies.threeBodies(), DT, POOL_SIZE);
        for (int i = 0; i < STEPS; i++) {
            one.step();
            many.step();
        }
        for (int i = 0; i < one.getNumBodies(); i++) {
            assertEquals(one.getBodies().get(i).getPos(), many.getBodies().get(i).getPos());
        }
    }
}
