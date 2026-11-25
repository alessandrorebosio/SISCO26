package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulationType;
import it.unibo.sisco.model.impl.body.TestBodies;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class NBodySimulationsTest {

    private final List<Body> bodies = TestBodies.threeBodies();
    private final List<NBodySimulation> created = new ArrayList<>();

    @AfterEach
    void closeSimulations() {
        this.created.forEach(NBodySimulation::close);
    }

    private NBodySimulation track(final NBodySimulation simulation) {
        this.created.add(simulation);
        return simulation;
    }

    @Test
    void ofChoosesTheMatchingImplementation() {
        assertInstanceOf(SequentialNBodySimulation.class,
                track(NBodySimulations.of(SimulationType.SEQUENTIAL, this.bodies, 2)));
        assertInstanceOf(ThreadNBodySimulation.class,
                track(NBodySimulations.of(SimulationType.THREADS, this.bodies, 2)));
        assertInstanceOf(ExecutorsNBodySimulation.class,
                track(NBodySimulations.of(SimulationType.EXECUTORS, this.bodies, 2)));
    }

    @Test
    void ofUsesTheDefaultTimeStep() {
        final AbstractNBodySimulation sim = (AbstractNBodySimulation) track(
                NBodySimulations.of(SimulationType.EXECUTORS, this.bodies, 2));
        assertEquals(AbstractNBodySimulation.DEFAULT_DT, sim.getTimeStep());
    }

    @Test
    void sequentialSimulationIgnoresTheNumberOfThreads() {
        assertInstanceOf(SequentialNBodySimulation.class,
                track(NBodySimulations.of(SimulationType.SEQUENTIAL, this.bodies, 0)));
    }

    @Test
    void parallelSimulationsRejectBadThreadCounts() {
        assertThrows(IllegalArgumentException.class,
                () -> track(NBodySimulations.of(SimulationType.THREADS, this.bodies, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> track(NBodySimulations.of(SimulationType.EXECUTORS, this.bodies, 0)));
    }

    @Test
    void builtSimulationsUseTheGivenBodies() {
        final NBodySimulation sim = track(NBodySimulations.of(SimulationType.EXECUTORS, this.bodies, 2));
        assertEquals(this.bodies.size(), sim.getNumBodies());
    }
}
