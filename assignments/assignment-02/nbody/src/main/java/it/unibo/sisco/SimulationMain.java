package it.unibo.sisco;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.impl.SimulationController;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.impl.body.BodySets;
import it.unibo.sisco.model.impl.simulation.NBodySimulations;
import it.unibo.sisco.view.api.SimulationView;
import it.unibo.sisco.view.impl.ConsoleSimulationView;
import it.unibo.sisco.view.impl.SwingSimulationView;

import java.util.List;

/**
 * Entry point of the N-body simulation. The arguments choose where it runs and
 * which implementation is used: {@code [cli] [sequential|threads|executors] [numThreads]}.
 *
 * <p>
 * By default it opens the Swing GUI, which displays every step and is driven
 * by its Start, Stop and Step buttons; with {@value LaunchOptions#CLI_FLAG} it
 * runs every step at once on the terminal and only logs the elapsed time.
 */
public final class SimulationMain {

    private static final int WINDOW_SIZE = 620;
    private static final int N_BODIES = 5000;
    private static final int N_STEPS = 100;

    private SimulationMain() {
    }

    /**
     * Runs the simulation chosen by the arguments.
     *
     * @param args {@code [cli] [sequential|threads|executors] [numThreads]}, in
     *             any order
     * @throws InterruptedException if the main thread is interrupted while the
     *                              simulation runs
     */
    public static void main(final String[] args) throws InterruptedException {
        final LaunchOptions options = LaunchOptions.parse(args);
        final List<Body> bodies = BodySets.random(N_BODIES);
        final SimulationView view = options.cli()
                ? new ConsoleSimulationView()
                : new SwingSimulationView(WINDOW_SIZE, WINDOW_SIZE);
        try (NBodySimulation simulation = NBodySimulations.of(options.simulation(), bodies, options.numThreads())) {
            final Controller controller = new SimulationController(view, simulation, N_STEPS);
            view.setController(controller);
            if (options.cli()) {
                controller.start();
            }
            controller.awaitTermination();
        }
    }
}
