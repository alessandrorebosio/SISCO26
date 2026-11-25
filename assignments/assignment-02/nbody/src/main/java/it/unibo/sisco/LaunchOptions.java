package it.unibo.sisco;

import it.unibo.sisco.model.impl.simulation.NBodySimulations;

import java.util.OptionalInt;

/**
 * The options given on the command line, in the form
 * {@code [cli] [sequential|threads|executors] [numThreads]}.
 *
 * @param cli        whether the simulation runs on the terminal instead of the GUI
 * @param simulation the name of the simulation to run, as accepted by
 *                   {@link NBodySimulations#byName}
 * @param numThreads the number of threads of the simulation, empty to use the
 *                   default one
 */
public record LaunchOptions(boolean cli, String simulation, OptionalInt numThreads) {

    /** Argument that runs the simulation on the terminal. */
    public static final String CLI_FLAG = "cli";

    /**
     * Reads the options from the command-line arguments, in any order: an
     * argument made only of digits is the number of threads, any other one is
     * the name of the simulation. Without arguments the simulation runs with the
     * GUI and the {@value NBodySimulations#THREADS} implementation, with the
     * default number of threads.
     *
     * @param args the command-line arguments
     * @return the options
     * @throws IllegalArgumentException if the number of threads is not positive
     */
    public static LaunchOptions parse(final String... args) {
        boolean cli = false;
        String simulation = NBodySimulations.THREADS;
        OptionalInt numThreads = OptionalInt.empty();
        for (final String arg : args) {
            if (CLI_FLAG.equalsIgnoreCase(arg)) {
                cli = true;
            } else if (arg.chars().allMatch(Character::isDigit) && !arg.isEmpty()) {
                numThreads = OptionalInt.of(positive(arg));
            } else {
                simulation = arg;
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
