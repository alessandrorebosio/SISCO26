package it.unibo.sisco.model.impl.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
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
    void byNameChoosesTheImplementationIgnoringCase() {
        assertInstanceOf(SequentialNBodySimulation.class, NBodySimulations.byName("sequential", this.bodies));
        assertInstanceOf(ThreadNBodySimulation.class, NBodySimulations.byName("THREADS", this.bodies));
        assertInstanceOf(ExecutorsNBodySimulation.class, NBodySimulations.byName("Executors", this.bodies));
    }

    @Test
    void byNameRejectsUnknownNames() {
        assertThrows(IllegalArgumentException.class, () -> NBodySimulations.byName("gpu", this.bodies));
    }

    @Test
    void byNameWithThreadsBuildsTheParallelImplementations() {
        assertInstanceOf(ThreadNBodySimulation.class, NBodySimulations.byName(NBodySimulations.THREADS, this.bodies, 2));
        assertInstanceOf(ExecutorsNBodySimulation.class,
                NBodySimulations.byName(NBodySimulations.EXECUTORS, this.bodies, 2));
    }

    @Test
    void byNameWithThreadsRejectsTheSequentialSimulationAndBadCounts() {
        assertThrows(IllegalArgumentException.class,
                () -> NBodySimulations.byName(NBodySimulations.SEQUENTIAL, this.bodies, 2));
        assertThrows(IllegalArgumentException.class,
                () -> NBodySimulations.byName(NBodySimulations.EXECUTORS, this.bodies, 0));
        assertThrows(IllegalArgumentException.class, () -> NBodySimulations.byName("gpu", this.bodies, 2));
    }

    @Test
    void builtSimulationsUseTheGivenBodies() {
        final NBodySimulation sim = NBodySimulations.byName(NBodySimulations.EXECUTORS, this.bodies);
        assertEquals(this.bodies.size(), sim.getNumBodies());
    }
}
