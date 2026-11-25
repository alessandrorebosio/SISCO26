package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.ControlState;
import it.unibo.sisco.controller.api.SimulationControl;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementation of {@link SimulationControl} as a monitor: every method is
 * {@code synchronized} and {@link #awaitNextStep()} waits on the monitor while
 * the simulation is ready or paused.
 */
public final class MonitorSimulationControl implements SimulationControl {

    private final Consumer<ControlState> onChange;
    private ControlState state = ControlState.READY;

    /**
     * Creates a monitor in the {@link ControlState#READY} state.
     *
     * @param onChange called, while holding the lock, with the new state at every
     *                 change: it must return quickly and never call this monitor
     *                 from another thread
     */
    public MonitorSimulationControl(final Consumer<ControlState> onChange) {
        this.onChange = Objects.requireNonNull(onChange);
    }

    @Override
    public synchronized void start() {
        if (this.state == ControlState.READY) {
            change(ControlState.RUNNING);
        }
    }

    @Override
    public synchronized void pause() {
        if (this.state == ControlState.RUNNING) {
            change(ControlState.PAUSED);
        }
    }

    @Override
    public synchronized void resume() {
        if (this.state == ControlState.PAUSED) {
            change(ControlState.RUNNING);
        }
    }

    @Override
    public synchronized void step() {
        if (this.state == ControlState.READY || this.state == ControlState.PAUSED) {
            change(ControlState.STEPPING);
        }
    }

    @Override
    public synchronized void stop() {
        if (this.state != ControlState.STOPPED) {
            change(ControlState.STOPPED);
        }
    }

    @Override
    public synchronized ControlState getState() {
        return this.state;
    }

    @Override
    public synchronized boolean awaitNextStep() throws InterruptedException {
        while (this.state == ControlState.READY || this.state == ControlState.PAUSED) {
            wait();
        }
        return this.state != ControlState.STOPPED;
    }

    @Override
    public synchronized void stepDone() {
        if (this.state == ControlState.STEPPING) {
            change(ControlState.PAUSED);
        }
    }

    private void change(final ControlState to) {
        this.state = to;
        this.onChange.accept(to);
        notifyAll();
    }
}
