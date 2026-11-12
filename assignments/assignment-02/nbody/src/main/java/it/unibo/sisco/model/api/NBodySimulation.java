package it.unibo.sisco.model.api;

import java.util.List;

/**
 * An N-body simulation that advances in discrete steps of virtual time.
 *
 * <p>The simulation is driven by a single thread, which calls {@link #step()} and reads the
 * state between two steps. Implementations may use other threads inside {@link #step()},
 * but every step is complete when the method returns.
 */
public interface NBodySimulation {

    /**
     * Executes one step of the simulation: updates the velocity of every body according to
     * the forces exerted by the others, then updates the positions of all the bodies and
     * advances the virtual time by one time step.
     *
     * <p>The velocities are computed from the positions at the beginning of the step, so no
     * position may change before every velocity has been updated.
     */
    void step();

    /**
     * Returns the bodies of the simulation.
     *
     * <p>The list cannot be modified, but its bodies are the live ones: their state changes
     * at every step, so it must be read only between two steps.
     *
     * @return an unmodifiable list of the bodies, always in the same order
     */
    List<Body> getBodies();

    /**
     * Returns the number of bodies of the simulation.
     *
     * @return the number of bodies
     */
    int getNumBodies();

    /**
     * Returns the current virtual time, that is the number of steps executed times the time
     * step.
     *
     * @return the virtual time, {@code 0} before the first step
     */
    double getVirtualTime();

    /**
     * Returns the number of steps executed so far.
     *
     * @return the number of steps executed, {@code 0} before the first step
     */
    long getIteration();
}
