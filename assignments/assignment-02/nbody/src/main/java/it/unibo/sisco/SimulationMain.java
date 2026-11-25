package it.unibo.sisco;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.view.impl.ConsoleSimulationView;

/**
 * Entry point of the N-body simulation without GUI: it runs the sequential
 * simulation of 5000 random bodies for 100 steps and logs the elapsed time.
 * Start it with {@code ./gradlew run}.
 */
public final class SimulationMain {

    private SimulationMain() {
    }

    /**
     * Runs the simulation and logs its duration.
     *
     * @param args command-line arguments, ignored
     */
    public static void main(final String[] args) {
        final Controller simulation = new SimulationController();
        simulation.attachView(new ConsoleSimulationView(simulation, 5000, 100));
        simulation.run();
    }
}
