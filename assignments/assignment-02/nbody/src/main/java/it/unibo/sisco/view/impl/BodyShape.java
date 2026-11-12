package it.unibo.sisco.view.impl;

/**
 * Snapshot of a body taken for rendering, decoupled from the live model.
 *
 * @param x the x coordinate of the body
 * @param y the y coordinate of the body
 * @param radius the radius of the body
 */
record BodyShape(double x, double y, double radius) {
}
