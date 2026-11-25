package it.unibo.sisco.controller.api;

import java.util.Set;

/**
 * Events that control the lifecycle of a simulation.
 */
public enum SimulationEvents {

    /**
     * Configures the simulation. Requires a {@link Configuration} payload.
     */
    SET_CONFIGURATION,

    /**
     * Starts or resumes continuous execution.
     */
    START,

    /**
     * Pauses continuous execution after the current step.
     */
    PAUSE,

    /**
     * Executes one step while configured or paused.
     */
    STEP;

    /**
     * Returns the events that may follow this event.
     *
     * @return the allowed next events
     */
    public Set<SimulationEvents> getNextPossibleEvents() {
        return switch (this) {
            case SET_CONFIGURATION -> Set.of(START, STEP);
            case START -> Set.of(PAUSE);
            case PAUSE -> Set.of(START, STEP);
            case STEP -> Set.of(STEP, START);
        };
    }
}
