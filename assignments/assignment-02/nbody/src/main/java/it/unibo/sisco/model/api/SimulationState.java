package it.unibo.sisco.model.api;

import java.util.List;

/**
 * Immutable snapshot of a simulation, taken between two steps: it can be kept
 * and read by any thread without affecting the simulation.
 *
 * @param bodies    the states of the bodies, in the same order as
 *                  {@link NBodySimulation#getBodies()}
 * @param vt        the virtual time
 * @param iteration the number of steps executed
 */
public record SimulationState(List<BodyState> bodies, double vt, long iteration) {

    /**
     * Builds a snapshot, copying the list of bodies.
     *
     * @param bodies    the states of the bodies
     * @param vt        the virtual time
     * @param iteration the number of steps executed
     */
    public SimulationState {
        bodies = List.copyOf(bodies);
    }
}
