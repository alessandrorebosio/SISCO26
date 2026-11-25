package it.unibo.sisco.model.impl.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.concurrency.Barrier;
import it.unibo.sisco.model.api.concurrency.Worker;
import it.unibo.sisco.model.impl.concurrency.BodyWorker;
import it.unibo.sisco.model.impl.concurrency.MonitorBarrier;

/**
 * Thread-based implementation of the N-body simulation using Java platform
 * threads and a custom monitor barrier: the bodies are split into contiguous
 * blocks of almost the same size, one for each {@link BodyWorker}.
 *
 * <p>
 * The workers are started when the simulation is built and live as long as the
 * JVM. At every step the thread that calls {@link #step()} only waits on the
 * barrier with them: it starts the two phases and waits for the end of the
 * step, without updating any body.
 */
public final class ThreadNBodySimulation extends AbstractNBodySimulation {

    private final Barrier barrier;
    private final List<Worker> workers;

    /**
     * Builds a simulation over a set of bodies, with the given time step, and
     * starts its workers, each on its own thread. At most {@code numThreads}
     * threads are created: if there are fewer bodies than threads, one thread is
     * created for each body.
     *
     * @param bodies     the bodies of the simulation
     * @param dt         the virtual time elapsed at each step
     * @param numThreads the maximum number of threads
     * @throws IllegalArgumentException if {@code dt} or {@code numThreads} is not
     *                                  positive
     */
    public ThreadNBodySimulation(final List<? extends Body> bodies, final double dt, final int numThreads) {
        super(bodies, dt);
        if (numThreads < 1) {
            throw new IllegalArgumentException("The number of threads must be positive: " + numThreads);
        }
        final int n = getNumBodies();
        final int k = Math.min(n, numThreads);
        this.barrier = new MonitorBarrier(k + 1);
        final List<Worker> created = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            created.add(new BodyWorker("BodyWorker-" + i, getBodies().subList(i * n / k, (i + 1) * n / k),
                    this.barrier, this::updateVelocity, this::updatePosition));
        }
        this.workers = List.copyOf(created);
        this.workers.forEach(Worker::start);
    }

    /**
     * Stops every worker: a step started after this call fails with an
     * {@link IllegalStateException}.
     */
    @Override
    public void close() {
        this.workers.forEach(Worker::stop);
    }

    @Override
    protected void computeStep() {
        try {
            // start velocities, start positions, wait for the end of the step
            this.barrier.await();
            this.barrier.await();
            this.barrier.await();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Simulation step interrupted", e);
        } catch (final BrokenBarrierException e) {
            throw new IllegalStateException("Simulation barrier broken", e);
        }
    }
}
