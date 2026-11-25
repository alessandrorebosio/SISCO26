package it.unibo.sisco.model.api.concurrency;

import java.util.concurrent.BrokenBarrierException;

/**
 * A synchronization aid that allows a set of threads to all wait for each other
 * to reach a common execution point.
 */
public interface Barrier {

    /**
     * Waits until all participating threads have invoked {@code await} on this
     * barrier.
     *
     * @throws InterruptedException   if the current thread is interrupted while
     *                                waiting
     * @throws BrokenBarrierException if the barrier is broken while waiting
     */
    void await() throws InterruptedException, BrokenBarrierException;

    /**
     * Breaks the barrier: the threads waiting on it, and every later call to
     * {@link #await()}, throw {@link BrokenBarrierException}. Breaking a broken
     * barrier has no effect.
     */
    void breakBarrier();
}
