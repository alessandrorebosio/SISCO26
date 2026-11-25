package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.impl.body.TestBodies;

import java.util.List;
import org.junit.jupiter.api.Test;

class ThreadNBodySimulationTest extends AbstractNBodySimulationTest {

    private static final int MANY_THREADS = 16;

    @Override
    protected AbstractNBodySimulation newSimulation(final List<? extends Body> bodies, final double dt) {
        return new ThreadNBodySimulation(bodies, dt, Runtime.getRuntime().availableProcessors());
    }

    @Test
    void nonPositiveNumberOfThreadsIsRejected() {
        final List<Body> bodies = TestBodies.twoBodies();
        assertThrows(IllegalArgumentException.class, () -> new ThreadNBodySimulation(bodies, DT, 0));
    }

    @Test
    void moreThreadsThanBodiesGiveTheSameResultAsOneThread() {
        final NBodySimulation one = track(new ThreadNBodySimulation(TestBodies.threeBodies(), DT, 1));
        final NBodySimulation many = track(new ThreadNBodySimulation(TestBodies.threeBodies(), DT, MANY_THREADS));
        for (int i = 0; i < STEPS; i++) {
            one.step();
            many.step();
        }
        for (int i = 0; i < one.getNumBodies(); i++) {
            assertEquals(one.getBodies().get(i).getPos(), many.getBodies().get(i).getPos());
        }
    }

    @Test
    void emptySetOfBodiesCanStep() {
        final NBodySimulation sim = track(new ThreadNBodySimulation(List.of(), DT, MANY_THREADS));
        sim.step();
        assertEquals(1, sim.getIteration());
    }

    @Test
    void stepAfterCloseFails() {
        final NBodySimulation sim = create(TestBodies.threeBodies(), DT);
        sim.close();
        assertThrows(IllegalStateException.class, sim::step);
    }
}
