package it.unibo.sisco.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.controller.api.ControlState;
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
    void controllerGivesItselfToTheViewAndWaitsReady() {
        final Controller controller = new SimulationController(this.view, this.simulation, N_STEPS);
        assertEquals(controller, this.view.controller);
        assertEquals(List.of(ControlState.READY), this.view.states);
        controller.stop();
    }

    @Test
    void startRunsEveryStepAndStops() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, N_STEPS);
        controller.start();
        controller.awaitTermination();
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), this.view.iterations);
        assertEquals(ControlState.STOPPED, this.view.states.getLast());
        assertEquals(0, this.view.over.getCount());
    }

    @Test
    void stepRunsOneStepAndPauses() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, N_STEPS);
        controller.step();
        assertTrue(this.view.firstDisplay.await(5, TimeUnit.SECONDS));
        Thread.sleep(PAUSE_MILLIS);
        assertEquals(List.of(1L), this.view.iterations);
        controller.stop();
        controller.awaitTermination();
        assertEquals(List.of(1L), this.view.iterations);
    }

    @Test
    void pauseSuspendsAndResumeContinues() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, Integer.MAX_VALUE);
        controller.start();
        assertTrue(this.view.firstDisplay.await(5, TimeUnit.SECONDS));
        controller.pause();
        Thread.sleep(PAUSE_MILLIS);
        final int displayed = this.view.iterations.size();
        Thread.sleep(PAUSE_MILLIS);
        assertEquals(displayed, this.view.iterations.size());
        controller.resume();
        controller.stop();
        controller.awaitTermination();
        assertEquals(ControlState.STOPPED, this.view.states.getLast());
    }

    @Test
    void stopBeforeAnyStepEndsTheSimulation() throws InterruptedException {
        final Controller controller = new SimulationController(this.view, this.simulation, N_STEPS);
        controller.stop();
        controller.awaitTermination();
        assertTrue(this.view.iterations.isEmpty());
        assertEquals(0, this.view.over.getCount());
    }

    private static final class RecordingView implements SimulationView {

        private final List<Long> iterations = new CopyOnWriteArrayList<>();
        private final List<ControlState> states = new CopyOnWriteArrayList<>();
        private final CountDownLatch firstDisplay = new CountDownLatch(1);
        private final CountDownLatch over = new CountDownLatch(1);
        private volatile Controller controller;

        @Override
        public void setController(final Controller newController) {
            this.controller = newController;
        }

        @Override
        public void controlChanged(final ControlState state) {
            this.states.add(state);
        }

        @Override
        public void init() {
            // Nothing to prepare
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
