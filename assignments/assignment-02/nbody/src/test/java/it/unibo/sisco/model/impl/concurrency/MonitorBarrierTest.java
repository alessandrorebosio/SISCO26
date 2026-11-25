package it.unibo.sisco.model.impl.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import it.unibo.sisco.model.api.concurrency.Barrier;

/**
 * Tests of {@link MonitorBarrier}: release of every party, reuse over many
 * rounds and breaking on interruption.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class MonitorBarrierTest {

    private static final int PARTIES = 4;
    private static final int ROUNDS = 200;

    @Test
    void rejectsLessThanOneParty() {
        assertThrows(IllegalArgumentException.class, () -> new MonitorBarrier(0));
    }

    @Test
    void singlePartyIsNeverSuspended() throws InterruptedException, BrokenBarrierException {
        final Barrier barrier = new MonitorBarrier(1);
        barrier.await();
        barrier.await();
    }

    @Test
    void noThreadPassesBeforeEveryoneArrivedInEveryRound() throws InterruptedException {
        final Barrier barrier = new MonitorBarrier(PARTIES);
        final AtomicInteger arrivals = new AtomicInteger();
        final Queue<Throwable> failures = new ConcurrentLinkedQueue<>();
        final List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < PARTIES; t++) {
            threads.add(Thread.ofPlatform().start(() -> {
                try {
                    for (int round = 1; round <= ROUNDS; round++) {
                        arrivals.incrementAndGet();
                        barrier.await();
                        // everybody has arrived at least up to this round
                        if (arrivals.get() < round * PARTIES) {
                            failures.add(new AssertionError("Released early in round " + round));
                        }
                    }
                } catch (final InterruptedException | BrokenBarrierException e) {
                    failures.add(e);
                }
            }));
        }
        for (final Thread thread : threads) {
            thread.join();
        }
        assertTrue(failures.isEmpty(), () -> "Failures: " + failures);
        assertEquals(PARTIES * ROUNDS, arrivals.get());
    }

    @Test
    void interruptingAWaitingThreadBreaksTheBarrier() throws InterruptedException {
        final Barrier barrier = new MonitorBarrier(3);
        final Queue<Throwable> outcomes = new ConcurrentLinkedQueue<>();
        final Runnable waiter = () -> {
            try {
                barrier.await();
            } catch (final InterruptedException | BrokenBarrierException e) {
                outcomes.add(e);
            }
        };
        final Thread interrupted = Thread.ofPlatform().start(waiter);
        final Thread other = Thread.ofPlatform().start(waiter);
        awaitWaiting(interrupted);
        awaitWaiting(other);

        interrupted.interrupt();
        interrupted.join();
        other.join();

        assertEquals(2, outcomes.size());
        assertTrue(outcomes.stream().anyMatch(InterruptedException.class::isInstance));
        assertTrue(outcomes.stream().anyMatch(BrokenBarrierException.class::isInstance));
        assertThrows(BrokenBarrierException.class, barrier::await);
    }

    private static void awaitWaiting(final Thread thread) throws InterruptedException {
        while (thread.getState() != Thread.State.WAITING) {
            Thread.sleep(1);
        }
    }
}
