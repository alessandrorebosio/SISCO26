package it.unibo.sisco.model.impl.simulation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import it.unibo.sisco.model.api.Body;

/**
 * N-body simulation that runs each phase of a step as a set of tasks, one for
 * each body, on a fixed pool of executor threads shared by all the steps.
 *
 * <p>
 * Each phase waits for all its tasks to finish before the next one starts, so
 * no position changes before every velocity has been updated. The threads of
 * the pool are daemons, so they never keep the JVM alive.
 */
public final class ExecutorsNBodySimulation extends AbstractNBodySimulation {

    private final ExecutorService executor;

    /**
     * Builds an executor-based simulation over a set of bodies, with the default
     * time step.
     *
     * @param bodies the bodies of the simulation
     */
    public ExecutorsNBodySimulation(final List<? extends Body> bodies) {
        this(bodies, DEFAULT_DT);
    }

    /**
     * Builds an executor-based simulation over a set of bodies, with a pool of
     * one thread more than the available processors.
     *
     * @param bodies the bodies of the simulation
     * @param dt     the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    public ExecutorsNBodySimulation(final List<? extends Body> bodies, final double dt) {
        super(bodies, dt);
        this.executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() + 1,
                Thread.ofPlatform().daemon().factory());
    }

    @Override
    protected void computeStep() {
        runPhase(this::updateVelocity);
        runPhase(this::updatePosition);
    }

    /**
     * Runs a phase of the step on every body, submitting one task for each body
     * to the pool, and returns only when all the tasks are complete.
     *
     * @param phase the operation to run on each body
     * @throws java.util.concurrent.CompletionException if a task fails
     */
    private void runPhase(final Consumer<Body> phase) {
        CompletableFuture.allOf(this.bodies.stream()
                .map(b -> CompletableFuture.runAsync(() -> phase.accept(b), this.executor))
                .toArray(CompletableFuture[]::new))
                .join();
    }
}
