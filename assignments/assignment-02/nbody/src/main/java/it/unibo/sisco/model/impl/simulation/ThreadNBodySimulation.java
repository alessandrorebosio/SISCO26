package it.unibo.sisco.model.impl.simulation;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import it.unibo.sisco.model.api.Body;

/**
 * Thread-based implementation of the N-body simulation using Java threads directly.
 * Each worker thread is responsible for one body and synchronizes with the other workers
 * between the two phases of each step.
 */
public final class ThreadNBodySimulation extends AbstractNBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;
    //Multiple worker threads, initially one per body, and a shared barrier for synchronization.
    private final List<BodyWorker> workers;
    private final CyclicBarrier barrier;
    private boolean started;

    /**
     * Builds a simulation over a set of bodies, with the default time step.
     *
     * @param bodies the bodies of the simulation
     */
    public ThreadNBodySimulation(final List<? extends Body> bodies) {
        this(bodies, DEFAULT_DT);
    }

    /**
     * Builds a simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    public ThreadNBodySimulation(final List<? extends Body> bodies, final double dt) {
        super(bodies, dt);
        final List<Body> simulationBodies = getBodies();
        this.barrier = new CyclicBarrier(simulationBodies.size() + 1);
        this.workers = simulationBodies.stream()
                .map(BodyWorker::new)
                .toList();
        this.started = false;
    }

    @Override
    protected void computeStep() {
            if (getNumBodies() == 0) {
                return;
            }
            if (!this.started) {
                this.workers.forEach(Thread::start);
                this.started = true;
            }
            try {
                // Release workers to start Phase 1 (updateVelocity)
                this.barrier.await();
                // Wait for Phase 1 to finish and release workers for Phase 2 (updatePosition)
                this.barrier.await();
                // Wait for Phase 2 to finish before completing the step
                this.barrier.await();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Simulation step interrupted", e);
            } catch (final BrokenBarrierException e) {
                throw new IllegalStateException("Simulation barrier broken", e);
            }
    }

    private final class BodyWorker extends Thread {

        private final Body body;

        private BodyWorker(final Body body) {
            super("BodyWorker-" + body.getId());
            this.body = body;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    barrier.await();
                    updateVelocity(this.body);
                    barrier.await();
                    updatePosition(this.body);
                    barrier.await();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (final BrokenBarrierException ignored) {
                // Barrier broken during shutdown
            }
        }
    }
}
