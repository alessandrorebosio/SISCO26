package it.unibo.sisco;

import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.impl.BodySets;
import it.unibo.sisco.model.impl.SequentialNBodySimulation;
import it.unibo.sisco.view.impl.SwingSimulationView;

/**
 * N-body simulation, simple sequential version with a GUI.
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
     * @param args unused
     */
    public static void main(final String[] args) {
        new SimulationController(
                new SequentialNBodySimulation(BodySets.random(N_BODIES)),
                new SwingSimulationView(WINDOW_SIZE, WINDOW_SIZE)).run(N_STEPS);
    }
}
