package it.unibo.sisco.model.api;

import java.io.Serializable;

/**
 * Immutable snapshot of a body, taken between two steps: it can be kept and
 * read by any thread without affecting the simulation.
 *
 * @param x      the x coordinate of the body
 * @param y      the y coordinate of the body
 * @param radius the radius of the body
 */
public record BodyState(double x, double y, double radius) implements Serializable {
}
