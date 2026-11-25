package it.unibo.sisco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.sisco.model.api.SimulationType;

import org.junit.jupiter.api.Test;

class LaunchOptionsTest {

    private static final int THREADS = 4;

    @Test
    void withoutArgumentsTheGuiRunsTheThreadsSimulationWithTheDefaultThreads() {
        assertEquals(new LaunchOptions(false, SimulationType.THREADS, LaunchOptions.DEFAULT_THREADS),
                LaunchOptions.parse());
    }

    @Test
    void cliFlagSelectsTheTerminal() {
        final LaunchOptions expected = new LaunchOptions(true, SimulationType.THREADS, LaunchOptions.DEFAULT_THREADS);
        assertEquals(expected, LaunchOptions.parse(LaunchOptions.CLI_FLAG));
        assertEquals(expected, LaunchOptions.parse("CLI"));
    }

    @Test
    void otherArgumentIsTheSimulationName() {
        assertEquals(new LaunchOptions(false, SimulationType.EXECUTORS, LaunchOptions.DEFAULT_THREADS),
                LaunchOptions.parse("executors"));
    }

    @Test
    void numericArgumentIsTheNumberOfThreads() {
        assertEquals(new LaunchOptions(false, SimulationType.THREADS, THREADS),
                LaunchOptions.parse(String.valueOf(THREADS)));
    }

    @Test
    void argumentsCanBeGivenInAnyOrder() {
        final LaunchOptions expected = new LaunchOptions(true, SimulationType.EXECUTORS, THREADS);
        final String threads = String.valueOf(THREADS);
        assertEquals(expected, LaunchOptions.parse(LaunchOptions.CLI_FLAG, "executors", threads));
        assertEquals(expected, LaunchOptions.parse(threads, "executors", LaunchOptions.CLI_FLAG));
    }

    @Test
    void unknownSimulationNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse("gpu"));
    }

    @Test
    void zeroOrTooManyThreadsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse("0"));
        assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse("99999999999"));
    }
}
