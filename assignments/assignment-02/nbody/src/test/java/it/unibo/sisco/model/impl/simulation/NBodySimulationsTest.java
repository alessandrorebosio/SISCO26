package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulationType;
import it.unibo.sisco.model.impl.body.TestBodies;

import java.util.List;
import org.junit.jupiter.api.Test;

class NBodySimulationsTest {

    private final List<Body> bodies = TestBodies.threeBodies();

    @Test
    void factoryMethodsBuildTheMatchingImplementation() {
        assertInstanceOf(SequentialNBodySimulation.class, NBodySimulations.sequential(this.bodies));
        assertInstanceOf(ThreadNBodySimulation.class, NBodySimulations.threads(this.bodies));
        assertInstanceOf(ExecutorsNBodySimulation.class, NBodySimulations.executors(this.bodies));
    }

    @Test
    void ofChoosesTheMatchingImplementation() {
        assertInstanceOf(SequentialNBodySimulation.class, NBodySimulations.of(SimulationType.SEQUENTIAL, this.bodies));
        assertInstanceOf(ThreadNBodySimulation.class, NBodySimulations.of(SimulationType.THREADS, this.bodies));
        assertInstanceOf(ExecutorsNBodySimulation.class, NBodySimulations.of(SimulationType.EXECUTORS, this.bodies));
    }

    @Test
    void ofWithThreadsBuildsTheParallelImplementations() {
        assertInstanceOf(ThreadNBodySimulation.class, NBodySimulations.of(SimulationType.THREADS, this.bodies, 2));
        assertInstanceOf(ExecutorsNBodySimulation.class, NBodySimulations.of(SimulationType.EXECUTORS, this.bodies, 2));
    }

    @Test
    void ofWithThreadsRejectsTheSequentialSimulationAndBadCounts() {
        assertThrows(IllegalArgumentException.class,
                () -> NBodySimulations.of(SimulationType.SEQUENTIAL, this.bodies, 2));
        assertThrows(IllegalArgumentException.class,
                () -> NBodySimulations.of(SimulationType.EXECUTORS, this.bodies, 0));
    }

    @Test
    void builtSimulationsUseTheGivenBodies() {
        final NBodySimulation sim = NBodySimulations.of(SimulationType.EXECUTORS, this.bodies);
        assertEquals(this.bodies.size(), sim.getNumBodies());
    }
}
