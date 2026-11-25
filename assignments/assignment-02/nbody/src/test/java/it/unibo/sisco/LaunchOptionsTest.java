package it.unibo.sisco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.impl.simulation.NBodySimulations;

import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class LaunchOptionsTest {

    private static final int THREADS = 4;

    @Test
    void withoutArgumentsTheGuiRunsTheThreadsSimulationWithTheDefaultThreads() {
        assertEquals(new LaunchOptions(false, NBodySimulations.THREADS, OptionalInt.empty()), LaunchOptions.parse());
    }

    @Test
    void cliFlagSelectsTheTerminal() {
        final LaunchOptions expected = new LaunchOptions(true, NBodySimulations.THREADS, OptionalInt.empty());
        assertEquals(expected, LaunchOptions.parse(LaunchOptions.CLI_FLAG));
        assertEquals(expected, LaunchOptions.parse("CLI"));
    }

    @Test
    void otherArgumentIsTheSimulationName() {
        assertEquals(new LaunchOptions(false, NBodySimulations.EXECUTORS, OptionalInt.empty()),
                LaunchOptions.parse(NBodySimulations.EXECUTORS));
    }

    @Test
    void numericArgumentIsTheNumberOfThreads() {
        assertEquals(new LaunchOptions(false, NBodySimulations.THREADS, OptionalInt.of(THREADS)),
                LaunchOptions.parse(String.valueOf(THREADS)));
    }

    @Test
    void argumentsCanBeGivenInAnyOrder() {
        final LaunchOptions expected = new LaunchOptions(true, NBodySimulations.EXECUTORS, OptionalInt.of(THREADS));
        final String threads = String.valueOf(THREADS);
        assertEquals(expected, LaunchOptions.parse(LaunchOptions.CLI_FLAG, NBodySimulations.EXECUTORS, threads));
        assertEquals(expected, LaunchOptions.parse(threads, NBodySimulations.EXECUTORS, LaunchOptions.CLI_FLAG));
    }

    @Test
    void zeroOrTooManyThreadsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse("0"));
        assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse("99999999999"));
    }
}
