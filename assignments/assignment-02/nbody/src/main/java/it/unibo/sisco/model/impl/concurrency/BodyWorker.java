package it.unibo.sisco.model.impl.concurrency;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.function.Consumer;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.concurrency.Barrier;
import it.unibo.sisco.model.api.concurrency.Worker;

/**
 * {@link Worker} that runs the two phases of every step on a block of bodies,
 * on its own platform thread.
 *
 * <p>
 * At every step it waits on a shared {@link Barrier} before each phase and at
 * the end of the step, and between them it runs:
 *
 * <ol>
 * <li>{@code updateVelocity} on every body of the block;</li>
 * <li>{@code updatePosition} on every body of the block.</li>
 * </ol>
 *
 * <p>
 * The operations are given by the simulation, so the worker knows only when to
 * run them, not what they do. Its thread is a daemon, so it never keeps the
 * JVM alive. Whatever makes the worker leave the loop (a call to
 * {@link #stop()}, an error in a phase or a broken barrier), it breaks the
 * barrier, so no other thread waits for it forever.
 */
public final class BodyWorker implements Worker {

    private final List<Body> bodies;
    private final Barrier barrier;
    private final Consumer<Body> updateVelocity;
    private final Consumer<Body> updatePosition;
    private final Thread thread;

    /**
     * Builds a worker for a block of bodies. Its thread is created but not
     * started.
     *
     * @param name           the name of the thread
     * @param bodies         the bodies this worker updates, and only them
     * @param barrier        the barrier shared with the other workers and the
     *                       thread that drives the simulation
     * @param updateVelocity the first phase of a step on a body
     * @param updatePosition the second phase of a step on a body
     */
    public BodyWorker(final String name, final List<Body> bodies, final Barrier barrier,
            final Consumer<Body> updateVelocity, final Consumer<Body> updatePosition) {
        this.bodies = List.copyOf(bodies);
        this.barrier = barrier;
        this.updateVelocity = updateVelocity;
        this.updatePosition = updatePosition;
        this.thread = Thread.ofPlatform().name(name).daemon().unstarted(this::work);
    }

    @Override
    public void start() {
        this.thread.start();
    }

    @Override
    public void stop() {
        this.thread.interrupt();
    }

    private void work() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.barrier.await();
                this.bodies.forEach(this.updateVelocity);
                this.barrier.await();
                this.bodies.forEach(this.updatePosition);
                this.barrier.await();
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final BrokenBarrierException ignored) {
            // Another worker has stopped or failed
        } finally {
            // This worker no longer takes part in the steps: nobody must wait for it
            this.barrier.breakBarrier();
        }
    }
}
