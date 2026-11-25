package it.unibo.sisco.controller.api;

import it.unibo.sisco.model.api.SimulatorTypes;

/**
 * Configuration of a simulation, as chosen by the user.
 *
 * @param simulator the selected simulator implementation
 * @param bodies the number of bodies, greater than 0
 * @param iterations the number of iterations, greater than 0
 */
public record Configuration(SimulatorTypes simulator, int bodies, int iterations) {
}