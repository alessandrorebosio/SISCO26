package it.unibo.sisco.view.impl;

import java.util.List;
import java.util.Scanner;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.SimulatorTypes;
import it.unibo.sisco.view.api.SimulationView;

/**
 * ConsoleSimulationView
 */
public class ConsoleSimulationView implements SimulationView {

    private final Controller controller;

    /**
     * Creates a console view connected to the given controller.
     *
     * @param controller the controller used to select the simulator
     */
    public ConsoleSimulationView(final Controller controller) {
        this.controller = controller;
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

        this.controller.setSimulator(switch (choice) {
            case 1 -> SimulatorTypes.SEQUENTIAL;
            case 2 -> SimulatorTypes.N_SYSTEM_THREAD;
            case 3 -> SimulatorTypes.FIXED_SYSTEM_THREAD;
            case 4 -> SimulatorTypes.EXECUTORS;
            default -> SimulatorTypes.SEQUENTIAL;
        });
    }

    @Override
    public void display(List<Body> bodies, double vt, long iteration) {
        // Show nothing
    }

    @Override
    public void selectedSimulator(SimulatorTypes simulator) {
        System.out.println("SELECTED SIMULATOR: " + simulator);
    }

    @Override
    public void simulationOver(long timeElapsed) {
        System.out.println("Simulation over");
        System.out.println("Time elapsed: " + timeElapsed);
    }

}
