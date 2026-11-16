package it.unibo.sisco.model.impl.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import it.unibo.sisco.model.api.Barrier;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.impl.barrier.BarrierImpl;

public final class ThreadNBodySimu extends AbstractNBodySimulation {

    public static final double DEFAULT_DT = 0.001;

    private final List<BodyWorker> workers;
    private final Barrier barrier;
    private boolean started;

    public ThreadNBodySimu(final List<? extends Body> bodies) {
        this(bodies, DEFAULT_DT);
    }

    public ThreadNBodySimu(final List<? extends Body> bodies, final double dt) {
        this(bodies, dt, Runtime.getRuntime().availableProcessors() + 1);
    }

    /**
     * Costruttore con numero di worker configurabile (utile anche per benchmark/test).
     */
    public ThreadNBodySimu(
            final List<? extends Body> bodies,
            final double dt,
            final int numWorkers) {
        super(bodies, dt);
        final List<Body> simulationBodies = getBodies();
        final int nBodies = simulationBodies.size();

        // Se ci sono meno corpi dei thread richiesti, usa al massimo nBodies worker
        final int effectiveWorkers = nBodies == 0 ? 0 : Math.max(1, Math.min(numWorkers, nBodies));

        this.barrier = new BarrierImpl(effectiveWorkers + 1);
        this.workers = new ArrayList<>(effectiveWorkers);

        if (effectiveWorkers > 0) {
            final int baseChunkSize = nBodies / effectiveWorkers;
            final int remainder = nBodies % effectiveWorkers;
            int startIndex = 0;

            for (int i = 0; i < effectiveWorkers; i++) {
                final int currentChunkSize = baseChunkSize + (i < remainder ? 1 : 0);
                final int endIndex = startIndex + currentChunkSize;
                final List<Body> chunk = simulationBodies.subList(startIndex, endIndex);
                this.workers.add(new BodyWorker(chunk, i));
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
            this.workers.forEach(Thread::start);
            this.started = true;
        }
        try {
            this.barrier.await(); // Via alla Fase 1 (velocità)
            this.barrier.await(); // Fine Fase 1 -> Via alla Fase 2 (posizione)
            this.barrier.await(); // Fine Fase 2 -> Step completato
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
                // Chiusura pulita
            }
        }
    }
}