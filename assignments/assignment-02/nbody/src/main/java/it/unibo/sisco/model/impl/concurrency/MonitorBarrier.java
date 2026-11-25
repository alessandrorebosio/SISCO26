package it.unibo.sisco.model.impl.concurrency;

import java.util.concurrent.BrokenBarrierException;

import it.unibo.sisco.model.api.concurrency.Barrier;

/**
 * Cyclic barrier implemented as a monitor: the first {@code parties - 1}
 * threads that call {@link #await()} are suspended, the last one wakes them all
 * up and the barrier is reset for the next use.
 *
 * <p>
 * If a waiting thread is interrupted the barrier breaks: the other waiting
 * threads, and every later call, throw {@link BrokenBarrierException}.
 */
public final class MonitorBarrier implements Barrier {

    private final int parties;
    private int arrived;
    private long generation;
    private boolean broken;

    /**
     * Builds a barrier for the given number of threads.
     *
     * @param parties the number of threads that must call {@link #await()}
     *                before all of them are released
     * @throws IllegalArgumentException if {@code parties} is less than 1
     */
    public MonitorBarrier(final int parties) {
        if (parties < 1) {
            throw new IllegalArgumentException("The barrier needs at least one party");
        }
        this.parties = parties;
    }

    @Override
    public synchronized void await() throws InterruptedException, BrokenBarrierException {
        if (this.broken) {
            throw new BrokenBarrierException();
        }
        if (++this.arrived == this.parties) {
            release();
        } else {
            waitForRelease(this.generation);
        }
    }

    /**
     * Wakes up every waiting thread and resets the barrier for the next round.
     * Called by the last thread that arrives, while holding the lock.
     */
    private void release() {
        this.arrived = 0;
        this.generation++;
        notifyAll();
    }

    /**
     * Suspends the calling thread, while holding the lock, until the round it
     * arrived in is released or the barrier breaks.
     *
     * @param myGeneration the round the calling thread arrived in
     * @throws InterruptedException   if the thread is interrupted while waiting,
     *                                which breaks the barrier
     * @throws BrokenBarrierException if the barrier breaks before the round is
     *                                released
     */
    private void waitForRelease(final long myGeneration) throws InterruptedException, BrokenBarrierException {
        while (this.generation == myGeneration && !this.broken) {
            try {
                wait();
            } catch (final InterruptedException e) {
                breakBarrier();
                throw e;
            }
        }
        if (this.generation == myGeneration) {
            throw new BrokenBarrierException();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * It wakes up every waiting thread, so none of them stays blocked. It is
     * also called from {@link #await()}, which already holds the lock: monitors
     * are reentrant.
     */
    @Override
    public synchronized void breakBarrier() {
        this.broken = true;
        notifyAll();
    }
}
