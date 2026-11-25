package it.unibo.sisco.model.impl.simulation;

import it.unibo.sisco.model.api.Body;

import java.util.List;

class SequentialNBodySimulationTest extends AbstractNBodySimulationTest {

    @Override
    protected AbstractNBodySimulation create(final List<? extends Body> bodies) {
        return new SequentialNBodySimulation(bodies);
    }

    @Override
    protected AbstractNBodySimulation create(final List<? extends Body> bodies, final double dt) {
        return new SequentialNBodySimulation(bodies, dt);
    }
}
