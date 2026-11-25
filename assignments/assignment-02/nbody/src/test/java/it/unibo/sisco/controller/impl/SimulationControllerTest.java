package it.unibo.sisco.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.model.api.SimulationState;
import it.unibo.sisco.model.api.SimulationType;
import it.unibo.sisco.model.impl.body.TestBodies;
import it.unibo.sisco.model.impl.simulation.NBodySimulations;
import it.unibo.sisco.view.api.SimulationView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class SimulationControllerTest {

    private static final int N_STEPS = 5;
    private static final long PAUSE_MILLIS = 200;

    private final NBodySimulation simulation = NBodySimulations.of(SimulationType.SEQUENTIAL,
            TestBodies.threeBodies(), 1);
    private final RecordingView view = new RecordingView();

    @AfterEach
    void closeSimulation() {
        this.simulation.close();
    }

    @Test
    void nonPositiveNumberOfStepsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SimulationController(this.view, this.simulation, 0));
    }

    @Test
    void startRunsEveryStepAndEndsTheSimulation() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, N_STEPS);
        controller.start();
        controller.awaitTermination();
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), this.view.iterations);
        assertEquals(1, this.view.inits.get());
        assertEquals(0, this.view.over.getCount());
    }

    @Test
    void stepRunsOneStepAtATime() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, N_STEPS);
        for (int i = 0; i < N_STEPS; i++) {
            controller.step();
        }
        controller.awaitTermination();
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), this.view.iterations);
    }

    @Test
    void commandsAfterTheEndHaveNoEffect() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, 1);
        controller.step();
        controller.awaitTermination();
        controller.step();
        controller.start();
        assertEquals(1, this.simulation.getIteration());
    }

    @Test
    void stopPausesTheSimulation() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, Integer.MAX_VALUE);
        controller.start();
        assertTrue(this.view.firstDisplay.await(5, TimeUnit.SECONDS));
        controller.stop();
        // The loop leaves after the step in progress: from then on nothing is displayed
        Thread.sleep(PAUSE_MILLIS);
        final int displayed = this.view.iterations.size();
        Thread.sleep(PAUSE_MILLIS);
        assertEquals(displayed, this.view.iterations.size());
    }

    private static final class RecordingView implements SimulationView {

        private final List<Long> iterations = new CopyOnWriteArrayList<>();
        private final AtomicInteger inits = new AtomicInteger();
        private final CountDownLatch firstDisplay = new CountDownLatch(1);
        private final CountDownLatch over = new CountDownLatch(1);

        @Override
        public void setController(final Controller controller) {
            // Not needed by the tests
        }

        @Override
        public void init() {
            this.inits.incrementAndGet();
        }

        @Override
        public void display(final SimulationState state) {
            this.iterations.add(state.iteration());
            this.firstDisplay.countDown();
        }

        @Override
        public void simulationOver(final long timeElapsed) {
            this.over.countDown();
        }
    }
}
