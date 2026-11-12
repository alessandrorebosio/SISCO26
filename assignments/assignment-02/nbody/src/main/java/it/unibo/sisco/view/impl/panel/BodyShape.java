package it.unibo.sisco.view.impl.panel;

import java.io.Serializable;

/**
 * Snapshot of a body taken for rendering: being immutable and separate from the live model,
 * it can be passed safely to the Event Dispatch Thread.
 *
 * @param x the x coordinate of the body
 * @param y the y coordinate of the body
 * @param radius the radius of the body
 */
public record BodyShape(double x, double y, double radius) implements Serializable {
}
