package it.unibo.sisco.controller.impl;

import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.NBodySimulation;
import it.unibo.sisco.view.api.SimulationView;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of {@link Controller}: the steps run on a single
 * thread, one after the other, and the view displays the state reached after
 * each of them.
 *
 * <p>
 * The view is initialised before the first step. Since it is updated between
 * two steps, it always sees a consistent state of the bodies. The time reported
 * at the end counts only the steps, drawing included, not the pauses.
 */
public final class SimulationController implements Controller {

    private final SimulationView view;
    private final NBodySimulation simulation;
    private final int nSteps;
    private final ExecutorService loop = Executors.newSingleThreadExecutor(
            Thread.ofPlatform().name("SimulationLoop").daemon().factory());
    private final AtomicBoolean running = new AtomicBoolean();
    private final CountDownLatch over = new CountDownLatch(1);
    // Accessed only by the thread of the loop
    private int stepsDone;
    private long elapsedNanos;

    /**
     * Creates a controller that runs the given simulation and displays it on
     * the given view.
     *
     * @param view       the view that displays the simulation
     * @param simulation the simulation to run
     * @param nSteps     the number of steps after which the simulation is over
     * @throws IllegalArgumentException if {@code nSteps} is not positive
     */
    public SimulationController(final SimulationView view, final NBodySimulation simulation, final int nSteps) {
        if (nSteps < 1) {
            throw new IllegalArgumentException("The number of steps must be positive: " + nSteps);
        }
        this.view = Objects.requireNonNull(view);
        this.simulation = Objects.requireNonNull(simulation);
        this.nSteps = nSteps;
    }

    @Override
    public void start() {
        if (this.over.getCount() > 0 && this.running.compareAndSet(false, true)) {
            this.loop.execute(() -> {
                while (this.running.get() && this.over.getCount() > 0) {
                    doStep();
                }
            });
        }
    }

    @Override
    public void stop() {
        this.running.set(false);
    }

    @Override
    public void step() {
        if (this.over.getCount() > 0 && !this.running.get()) {
            this.loop.execute(this::doStep);
        }
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        this.over.await();
    }

    private void doStep() {
        if (this.over.getCount() == 0) {
            return;
        }
        if (this.stepsDone == 0) {
            this.view.init();
        }
        final long start = System.nanoTime();
        this.simulation.step();
        this.view.display(this.simulation.getState());
        this.elapsedNanos += System.nanoTime() - start;
        this.stepsDone++;
        if (this.stepsDone == this.nSteps) {
            this.view.simulationOver(TimeUnit.NANOSECONDS.toMillis(this.elapsedNanos));
            this.over.countDown();
        }
    }
}
