package it.unibo.sisco;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.SequentialNBodySimulation;
import java.util.logging.Logger;

/**
 * N-body simulation, simple sequential version without GUI.
 */
public final class SimulationMain {

    private static final Logger LOGGER = Logger.getLogger(SimulationMain.class.getName());
    private static final int N_BODIES = 5000;
    private static final int N_STEPS = 100;

    private SimulationMain() {
    }

    /**
     * Runs the simulation and logs its duration.
     *
     * @param args unused
     */
    public static void main(final String[] args) {
        final NBodySimulation sim = new SequentialNBodySimulation(BodySets.random(N_BODIES));
        final Controller controller = new SimulationController(sim);
        LOGGER.info(() -> "Starting simulation with " + sim.getNumBodies() + " bodies - " + N_STEPS + " steps...");
        final long startTime = System.currentTimeMillis();
        controller.run(N_STEPS);
        final long elapsed = System.currentTimeMillis() - startTime;
        LOGGER.info(() -> "Simulation completed in " + elapsed + " ms.");
    }
}
