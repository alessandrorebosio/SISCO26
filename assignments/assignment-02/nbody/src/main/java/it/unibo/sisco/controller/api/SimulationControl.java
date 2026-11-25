package it.unibo.sisco.controller.api;

/**
 * Holds the state of a simulation driven by the user, shared between the
 * thread that gives the commands and the thread that executes the steps.
 *
 * <p>
 * The commands change the state and return immediately. The thread of the
 * simulation calls {@link #awaitNextStep()} before each step and
 * {@link #stepDone()} after it. A command that is not allowed in the current
 * state has no effect; {@link #stop()} is allowed in every state.
 */
public interface SimulationControl {

    /** Starts the simulation, if it is ready. */
    void start();

    /** Suspends the simulation after the step in progress, if it is running. */
    void pause();

    /** Resumes the simulation, if it is paused. */
    void resume();

    /** Executes a single step, if the simulation is ready or paused. */
    void step();

    /** Stops the simulation for good, after the step in progress. */
    void stop();

    /**
     * Returns the current state.
     *
     * @return the current state
     */
    ControlState getState();

    /**
     * Waits until the next step can be executed, that is while the simulation is
     * ready or paused.
     *
     * @return {@code true} if the next step must be executed, {@code false} if
     *         the simulation is stopped
     * @throws InterruptedException if the calling thread is interrupted while
     *                              waiting
     */
    boolean awaitNextStep() throws InterruptedException;

    /**
     * Notifies that a step is complete: after a single step the simulation is
     * paused again.
     */
    void stepDone();
}
