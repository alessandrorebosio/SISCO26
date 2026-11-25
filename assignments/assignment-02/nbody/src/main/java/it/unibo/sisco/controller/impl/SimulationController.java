package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.api.SimulationControl;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link Controller}: a thread of the controller
 * executes the steps when the {@link SimulationControl} monitor allows it, and
 * the view displays the state reached after each of them.
 *
 * <p>
 * Since the view is updated between two steps, it always sees a consistent
 * state of the bodies. The time reported at the end counts only the steps,
 * drawing included, not the pauses.
 */
public final class SimulationController implements Controller {

    private final SimulationControl control;
    private final Thread loop;

    /**
     * Creates a controller that runs the given simulation and displays it on
     * the given view, and gives the view this controller. The simulation waits
     * for {@link #start()} or {@link #step()}.
     *
     * @param view       the view that displays the simulation
     * @param simulation the simulation to run
     * @param nSteps     the number of steps after which the simulation stops
     * @throws IllegalArgumentException if {@code nSteps} is not positive
     */
    public SimulationController(final SimulationView view, final NBodySimulation simulation, final int nSteps) {
        if (nSteps < 1) {
            throw new IllegalArgumentException("The number of steps must be positive: " + nSteps);
        }
        Objects.requireNonNull(simulation);
        view.setController(this);
        this.control = new MonitorSimulationControl(view::controlChanged);
        view.controlChanged(this.control.getState());
        this.loop = Thread.ofPlatform().name("SimulationLoop").daemon().start(() -> {
            long elapsedNanos = 0;
            try {
                for (int i = 0; i < nSteps && this.control.awaitNextStep(); i++) {
                    if (i == 0) {
                        view.init();
                    }
                    final long start = System.nanoTime();
                    simulation.step();
                    view.display(simulation.getState());
                    elapsedNanos += System.nanoTime() - start;
                    this.control.stepDone();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.control.stop();
            view.simulationOver(TimeUnit.NANOSECONDS.toMillis(elapsedNanos));
        });
    }

    @Override
    public void start() {
        this.control.start();
    }

    @Override
    public void pause() {
        this.control.pause();
    }

    @Override
    public void resume() {
        this.control.resume();
    }

    @Override
    public void step() {
        this.control.step();
    }

    @Override
    public void stop() {
        this.control.stop();
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        this.loop.join();
    }
}
