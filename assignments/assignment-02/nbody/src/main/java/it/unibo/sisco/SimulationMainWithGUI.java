package it.unibo.sisco;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.view.impl.SwingSimulationView;

/**
 * Entry point of the N-body simulation with GUI: it runs the sequential simulation of 5000
 * random bodies for 50,000 steps, displaying every step. Start it with
 * {@code ./gradlew runGui}.
 */
public final class SimulationMainWithGUI {

    private static final int WINDOW_SIZE = 620;

    private SimulationMainWithGUI() { }

    /**
     * Runs the simulation, displaying every step.
     *
     * @param args command-line arguments, ignored
     */
    public static void main(final String[] args) {
        final Controller simulation = new SimulationController();
        simulation.attachView(new SwingSimulationView(WINDOW_SIZE, WINDOW_SIZE));
        simulation.setSimulator(SimulatorTypes.SYSTEM_THREAD);
        simulation.run();
    }
}
