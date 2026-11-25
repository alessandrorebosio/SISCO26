package it.unibo.sisco.view.impl;

import it.unibo.sisco.model.api.SimulationState;
import it.unibo.sisco.view.api.SimulationView;

import java.util.logging.Logger;

/**
 * Terminal implementation of {@link SimulationView}: it logs only when the
 * simulation starts and how long it took, so that drawing does not affect the
 * measured time.
 */
public final class ConsoleSimulationView implements SimulationView {

    private static final Logger LOGGER = Logger.getLogger(ConsoleSimulationView.class.getName());

    /**
     * Creates a console view.
     */
    public ConsoleSimulationView() {
        // Nothing to set up: the logger is shared by every instance
    }

    @Override
    public void init() {
        LOGGER.info("Simulation started");
    }

    @Override
    public void display(final SimulationState state) {
        // Nothing to show between two steps
    }

    @Override
    public void simulationOver(final long timeElapsed) {
        LOGGER.info(() -> "Simulation over in " + timeElapsed + " ms");
    }
}
