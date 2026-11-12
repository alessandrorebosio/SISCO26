package it.unibo.sisco;

import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.SequentialNBodySimulation;
import it.unibo.sisco.view.impl.SwingSimulationView;

/**
 * Entry point of the N-body simulation with GUI: it runs the sequential simulation of 5000
 * random bodies for 50,000 steps, displaying every step. Start it with
 * {@code ./gradlew runGui}.
 */
public final class SimulationMainWithGUI {

    private static final int N_BODIES = 5000;
    private static final int N_STEPS = 50_000;
    private static final int WINDOW_SIZE = 620;

    private SimulationMainWithGUI() {
    }

    /**
     * Runs the simulation, displaying every step.
     *
     * @param args command-line arguments, ignored
     */
    public static void main(final String[] args) {
        new SimulationController(
                new SequentialNBodySimulation(BodySets.random(N_BODIES)),
                new SwingSimulationView(WINDOW_SIZE, WINDOW_SIZE)).run(N_STEPS);
    }
}
