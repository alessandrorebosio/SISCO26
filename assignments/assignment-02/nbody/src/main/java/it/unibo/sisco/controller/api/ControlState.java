package it.unibo.sisco.controller.api;

/**
 * The states of a simulation driven by the user.
 */
public enum ControlState {

    /** Created, waiting for the first command. */
    READY,

    /** Executing the steps one after the other. */
    RUNNING,

    /** Suspended between two steps. */
    PAUSED,

    /** Executing a single step, after which it is paused. */
    STEPPING,

    /** Over: no more steps are executed. */
    STOPPED
}
