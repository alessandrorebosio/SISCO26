package it.unibo.sisco;

import java.util.logging.Logger;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.ThreadNBodySimulation;

/**
 * Entry point of the N-body simulation without GUI: it runs the sequential simulation of
 * 5000 random bodies for 100 steps and logs the elapsed time. Start it with
 * {@code ./gradlew run}.
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
     * @param args command-line arguments, ignored
     */
    public static void main(final String[] args) {
        //final NBodySimulation sim = new SequentialNBodySimulation(BodySets.random(N_BODIES));
        final NBodySimulation sim = new ThreadNBodySimulation(BodySets.random(N_BODIES));

        final Controller controller = new SimulationController(sim);
        LOGGER.info(() -> "Starting simulation with " + sim.getNumBodies() + " bodies - " + N_STEPS + " steps...");
        final long startTime = System.currentTimeMillis();
        controller.run(N_STEPS);
        final long elapsed = System.currentTimeMillis() - startTime;
        LOGGER.info(() -> "Simulation completed in " + elapsed + " ms.");
    }
}
