package it.unibo.sisco.model.impl.simulation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import it.unibo.sisco.model.api.Body;

/**
 * N-body simulation that computes each phase with a pool of executor threads.
 *
 * <p>Velocity and position updates are performed in separate phases so that no position
 * is modified before every velocity has been updated.
 */
public class ExecutorsNBodySimulation extends AbstractNBodySimulation {

    /**
     * Default virtual time elapsed at each step.
     */
    public static final double DEFAULT_DT = 0.001;
    private final int threadNumber;

    /**
     * Builds an executor-based simulation over a set of bodies.
     *
     * @param bodies the bodies of the simulation
     * @param dt the virtual time elapsed at each step
     * @throws IllegalArgumentException if {@code dt} is not positive
     */
    public ExecutorsNBodySimulation(final List<? extends Body> bodies, final double dt) {
        super(bodies, dt);
        this.threadNumber = Runtime.getRuntime().availableProcessors() + 1;
    }

    /**
     * Builds an executor-based simulation using the default time step.
     *
     * @param bodies the bodies of the simulation
     */
    public ExecutorsNBodySimulation(List<? extends Body> bodies) {
        this(bodies, ExecutorsNBodySimulation.DEFAULT_DT);
    }

    @Override
    protected void computeStep() {
        try (
            final ExecutorService execVel = Executors.newFixedThreadPool(this.threadNumber);
            final ExecutorService execPos = Executors.newFixedThreadPool(this.threadNumber);
        ) {
            this.bodies.forEach(b ->
                execVel.submit(() -> updateVelocity(b))
            );
            execVel.shutdown();
            execVel.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            this.bodies.forEach(b ->
                execPos.submit(() -> updatePosition(b))
            );
            execPos.shutdown();
            execPos.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Simulation interrupted", e);
        } catch (final Exception e) {
            throw new IllegalStateException("Error during simulation", e);
        }
    }
}