package it.unibo.sisco;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.view.impl.ConsoleSimulationView;
import it.unibo.sisco.view.impl.SwingSimulationView;

/**
 * Entry point of the N-body simulation.
 *
 * <p>By default it opens the Swing GUI, which displays every step. With the
 * {@value #CLI_FLAG} argument it runs without GUI and logs the elapsed time
 * on the terminal instead. Start it with {@code ./gradlew run} or
 * {@code ./gradlew run --args=cli}.
 */
public final class SimulationMain {

    /** Command-line argument that selects the terminal version. */
    public static final String CLI_FLAG = "cli";

    private static final int WINDOW_SIZE = 620;

    private SimulationMain() {
    }

    /**
     * Runs the simulation with the GUI, or on the terminal if
     * {@value #CLI_FLAG} is given.
     *
     * @param args command-line arguments: none, or {@value #CLI_FLAG}
     */
    public static void main(final String[] args) {
        final Controller simulation = new SimulationController();
        if (args.length > 0 && CLI_FLAG.equals(args[0])) {
            simulation.attachView(new ConsoleSimulationView(simulation));
        } else {
            simulation.attachView(new SwingSimulationView(WINDOW_SIZE, WINDOW_SIZE));
            simulation.setSimulator(SimulatorTypes.FIXED_SYSTEM_THREAD);
        }
        simulation.run();
    }
}
