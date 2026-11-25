package it.unibo.sisco;

import it.unibo.sisco.model.api.SimulationType;

import java.util.Locale;

/**
 * The options given on the command line, in the form
 * {@code [cli] [sequential|threads|executors] [numThreads]}.
 *
 * @param cli        whether the simulation runs on the terminal instead of the GUI
 * @param simulation the implementation of the simulation to run
 * @param numThreads the number of threads of the simulation, ignored by the
 *                   sequential one
 */
public record LaunchOptions(boolean cli, SimulationType simulation, int numThreads) {

    /** Argument that runs the simulation on the terminal. */
    public static final String CLI_FLAG = "cli";

    /** Number of threads used when none is given: one for each processor. */
    public static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * Reads the options from the command-line arguments, in any order: an
     * argument made only of digits is the number of threads, any other one is
     * the name of the simulation, ignoring case.
     * Without arguments the simulation runs with the GUI and the
     * {@link SimulationType#THREADS} implementation, with
     * {@link #DEFAULT_THREADS} threads.
     *
     * @param args the command-line arguments
     * @return the options
     * @throws IllegalArgumentException if the number of threads is not positive
     *                                  or the name of the simulation is unknown
     */
    public static LaunchOptions parse(final String... args) {
        boolean cli = false;
        SimulationType simulation = SimulationType.THREADS;
        int numThreads = DEFAULT_THREADS;
        for (final String arg : args) {
            if (CLI_FLAG.equalsIgnoreCase(arg)) {
                cli = true;
            } else if (arg.chars().allMatch(Character::isDigit) && !arg.isEmpty()) {
                numThreads = positive(arg);
            } else {
                simulation = SimulationType.valueOf(arg.toUpperCase(Locale.ROOT));
            }
        }
        return new LaunchOptions(cli, simulation, numThreads);
    }

    private static int positive(final String arg) {
        final int value;
        try {
            value = Integer.parseInt(arg);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Too many threads: " + arg, e);
        }
        if (value < 1) {
            throw new IllegalArgumentException("The number of threads must be positive: " + arg);
        }
        return value;
    }
}
