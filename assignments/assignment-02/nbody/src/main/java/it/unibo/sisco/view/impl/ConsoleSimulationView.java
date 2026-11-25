package it.unibo.sisco.view.impl;

import java.util.List;
import java.util.Scanner;

import it.unibo.sisco.controller.api.Configuration;
import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.view.api.SimulationView;

/**
 * ConsoleSimulationView
 */
public class ConsoleSimulationView implements SimulationView {

    private final Controller controller;
    private final int nBodies;
    private final int iterations;

    /**
     * Creates a console view connected to the given controller.
     *
     * @param controller the controller used to select the simulator
     * @param nBodies the number of bodies in the simulation
     * @param iterations the number of simulation iterations
     */
    public ConsoleSimulationView(final Controller controller, final int nBodies, final int iterations) {
        this.controller = controller;
        this.nBodies = nBodies;
        this.iterations = iterations;
    }

    @Override
    public void init() {
        System.out.println("""
        SELECT THE SIMULATOR TO USE:
        1) Sequential
        2) N System Thread
        3) Fixed System Thread
        4) Java Executor Framework
        """);
        final Scanner sc = new Scanner(System.in);
        String line;
        do {
            System.out.print("> ");
            line = sc.nextLine().trim();
        } while (!line.equals("1") && !line.equals("2") && !line.equals("3") && !line.equals("4"));
        sc.close();
        final int choice = Integer.parseInt(line);

        SimulatorTypes sim = switch (choice) {
            case 1 -> SimulatorTypes.SEQUENTIAL;
            case 2 -> SimulatorTypes.N_SYSTEM_THREAD;
            case 3 -> SimulatorTypes.FIXED_SYSTEM_THREAD;
            case 4 -> SimulatorTypes.EXECUTORS;
            default -> SimulatorTypes.SEQUENTIAL;
        };

        this.controller.setConfiguration(new Configuration(sim, this.nBodies, this.iterations));
    }

    @Override
    public void display(List<Body> bodies, double vt, long iteration) {
        // Show nothing
    }

    @Override
    public void selectedConfiguration(final Configuration config) {
        System.out.println("SELECTED SIMULATOR: " + config.simulator());
        System.out.println("BODIES: " + config.bodies());
        System.out.println("ITERATIONS: " + config.iterations());
    }

    @Override
    public void simulationOver(final long timeElapsed) {
        System.out.println("Simulation over");
        System.out.println("Time elapsed: " + timeElapsed);
    }

}
