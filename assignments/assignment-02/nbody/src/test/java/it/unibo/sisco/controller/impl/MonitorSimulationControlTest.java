package it.unibo.sisco.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.controller.api.ControlState;
import it.unibo.sisco.controller.api.SimulationControl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class MonitorSimulationControlTest {

    private final List<ControlState> changes = new CopyOnWriteArrayList<>();
    private final SimulationControl control = new MonitorSimulationControl(this.changes::add);

    @Test
    void startsReadyAndRunsAfterStart() throws InterruptedException {
        assertEquals(ControlState.READY, this.control.getState());
        this.control.start();
        assertTrue(this.control.awaitNextStep());
        assertEquals(List.of(ControlState.RUNNING), this.changes);
    }

    @Test
    void pauseAndResumeSwitchBetweenRunningAndPaused() {
        this.control.start();
        this.control.pause();
        assertEquals(ControlState.PAUSED, this.control.getState());
        this.control.resume();
        assertEquals(List.of(ControlState.RUNNING, ControlState.PAUSED, ControlState.RUNNING), this.changes);
    }

    @Test
    void stepRunsOneStepAndThenPauses() throws InterruptedException {
        this.control.step();
        assertTrue(this.control.awaitNextStep());
        this.control.stepDone();
        assertEquals(List.of(ControlState.STEPPING, ControlState.PAUSED), this.changes);
    }

    @Test
    void commandsNotAllowedInTheCurrentStateHaveNoEffect() {
        this.control.pause();
        this.control.resume();
        this.control.stepDone();
        assertEquals(ControlState.READY, this.control.getState());
        this.control.start();
        this.control.start();
        this.control.step();
        assertEquals(List.of(ControlState.RUNNING), this.changes);
    }

    @Test
    void stopIsFinalAndEndsTheSteps() throws InterruptedException {
        this.control.stop();
        this.control.start();
        this.control.step();
        this.control.stop();
        assertFalse(this.control.awaitNextStep());
        assertEquals(List.of(ControlState.STOPPED), this.changes);
    }

    @Test
    void awaitNextStepWaitsWhilePaused() throws InterruptedException {
        final CountDownLatch waiting = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean();
        final Thread simulation = Thread.ofPlatform().start(() -> {
            try {
                waiting.countDown();
                result.set(this.control.awaitNextStep());
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        assertTrue(waiting.await(1, TimeUnit.SECONDS));
        while (simulation.getState() != Thread.State.WAITING) {
            Thread.onSpinWait();
        }
        this.control.start();
        simulation.join();
        assertTrue(result.get());
    }
}
