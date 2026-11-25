package it.unibo.sisco.model.impl.barrier;

import java.util.concurrent.BrokenBarrierException;

import it.unibo.sisco.model.api.Barrier;

/**
 * Monitor-based implementation of a {@link Barrier} that blocks participating threads
 * until all of them have reached the synchronization point.
 */
public class MonitorBarrier implements Barrier {

    private final int nPartecipants;
    private int nArrived;
    private int generation;

    /**
     * Builds a new barrier for the specified number of participating threads.
     *
     * @param nPartecipants the total number of threads that must invoke {@link #await()}
     *                      before any of them are released
     */
    public MonitorBarrier(final int nPartecipants) {
        this.nPartecipants = nPartecipants;
        this.nArrived = 0;
        this.generation = 0;
    }

    /**
     * Waits until all participating threads have invoked {@code await} on this barrier.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws BrokenBarrierException if the barrier is broken while waiting
     */
    @Override
    public synchronized void await() throws InterruptedException, BrokenBarrierException {
        this.nArrived++;
        final int currentG = this.generation;
        
        if(this.nArrived == this.nPartecipants) {
            nArrived = 0;
            generation++;
            notifyAll();
        } else {
            while (currentG == this.generation) {
                wait();
            }
        }
    }
}