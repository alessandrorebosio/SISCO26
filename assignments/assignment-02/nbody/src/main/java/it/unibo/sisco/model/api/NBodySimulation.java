package it.unibo.sisco.model.api;

import java.util.List;

/**
 * An N-body simulation that advances in discrete steps of virtual time.
 */
public interface NBodySimulation {

    /**
     * Executes one step of the simulation: updates the velocity of every body
     * according to the forces exerted by the others, then updates the positions
     * and advances the virtual time.
     */
    void step();

    /**
     * Returns the bodies of the simulation.
     *
     * @return an unmodifiable list of the bodies
     */
    List<Body> getBodies();

    /**
     * Returns the number of bodies of the simulation.
     *
     * @return the number of bodies
     */
    int getNumBodies();

    /**
     * Returns the current virtual time.
     *
     * @return the virtual time
     */
    double getVirtualTime();

    /**
     * Returns the number of steps executed so far.
     *
     * @return the number of steps executed
     */
    long getIteration();
}
