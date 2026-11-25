package it.unibo.sisco.model.impl.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import it.unibo.sisco.model.api.Barrier;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.impl.barrier.MonitorBarrier;

/**
 * Thread-based implementation of the N-body simulation using Java platform
 * threads and a custom monitor barrier.
 */
public final class FixedThreadNBodySimulation extends AbstractNBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;

    private final List<BodyWorker> workers;
    private final Barrier barrier;
    private boolean started;

    /**
     * Builds a simulation over a set of bodies, with the default time step.
     *
     * @param bodies the bodies of the simulation
     */
    public FixedThreadNBodySimulation(final List<? extends Body> bodies) {
        this(bodies, DEFAULT_DT);
    }

    /**
     * Builds a simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt     the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    public FixedThreadNBodySimulation(final List<? extends Body> bodies, final double dt) {
        super(bodies, dt);
        final List<Body> simulationBodies = getBodies();
        final int nBodies = simulationBodies.size();
        final int numWorkers = Math.min(nBodies, Runtime.getRuntime().availableProcessors() + 1);

        this.barrier = new MonitorBarrier(numWorkers + 1);
        this.workers = new ArrayList<>(numWorkers);

        if (numWorkers > 0) {
            final int baseChunkSize = nBodies / numWorkers;
            final int remainder = nBodies % numWorkers;
            int startIndex = 0;

            for (int i = 0; i < numWorkers; i++) {
                final int endIndex = startIndex + baseChunkSize + (i < remainder ? 1 : 0);
                this.workers.add(new BodyWorker(simulationBodies.subList(startIndex, endIndex), i));
                startIndex = endIndex;
            }
        }
    }

    @Override
    protected void computeStep() {
        if (getNumBodies() == 0) {
            return;
        }
        if (!this.started) {
            for (final BodyWorker worker : this.workers) {
                worker.start();
            }
            this.started = true;
        }
        try {
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

    private final class BodyWorker extends Thread {

        private final List<Body> assignedBodies;

        private BodyWorker(final List<Body> assignedBodies, final int workerId) {
            super("BodyWorker-" + workerId);
            this.assignedBodies = assignedBodies;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    barrier.await();
                    for (final Body body : this.assignedBodies) {
                        updateVelocity(body);
                    }
                    barrier.await();
                    for (final Body body : this.assignedBodies) {
                        updatePosition(body);
                    }
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