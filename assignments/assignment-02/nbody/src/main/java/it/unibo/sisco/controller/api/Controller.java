package it.unibo.sisco.controller.api;

import it.unibo.sisco.view.api.SimulationView;

import java.util.Optional;

/**
 * Controller that drives an N-body simulation and, if present, keeps its view
 * up to date.
 */
public interface Controller {

    /**
     * Runs the simulation for its configured number of steps, in the calling
     * thread.
     *
     * <p>
     * The method returns only when every step has been executed.
     */
    void run();

    /**
     * Attaches a view to receive simulation updates.
     *
     * @param view the view to attach
     */
    void attachView(SimulationView view);

    /**
     * Handles a simulation lifecycle event.
     *
     * <p>
     * {@link SimulationEvents#SET_CONFIGURATION} requires a
     * {@link Configuration} payload. All other events require an empty payload.
     *
     * @param event the event to handle
     * @param data optional event data
     * @throws IllegalArgumentException if the payload does not match the event
     * @throws IllegalStateException if the event cannot follow the previous event or no steps remain
     */
    void handleEvent(SimulationEvents event, Optional<?> data);
}
