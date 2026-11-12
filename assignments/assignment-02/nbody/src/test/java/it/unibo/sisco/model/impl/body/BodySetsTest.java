package it.unibo.sisco.model.impl.body;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.sisco.model.api.Body;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BodySetsTest {

    private static final int N_BODIES = 200;
    private static final long SEED = 42;

    @Test
    void predefinedSetsHaveTheExpectedSize() {
        assertEquals(2, BodySets.twoBodies().size());
        assertEquals(3, BodySets.threeBodies().size());
    }

    @Test
    void randomSetHasTheRequestedSize() {
        assertEquals(N_BODIES, BodySets.random(N_BODIES, SEED).size());
        assertTrue(BodySets.random(0, SEED).isEmpty());
    }

    @Test
    void randomSetHasDistinctIds() {
        final List<Body> bodies = BodySets.random(N_BODIES, SEED);
        assertEquals(N_BODIES, bodies.stream().map(Body::getId).collect(Collectors.toSet()).size());
    }

    @Test
    void randomSetRespectsTheRanges() {
        for (final Body b : BodySets.random(N_BODIES, SEED)) {
            assertTrue(b.getPos().x() >= -1 && b.getPos().x() < 1, b::toString);
            assertTrue(b.getPos().y() >= -1 && b.getPos().y() < 1, b::toString);
            assertTrue(Math.abs(b.getVel().x()) <= 0.1, b::toString);
            assertTrue(Math.abs(b.getVel().y()) <= 0.1, b::toString);
            assertTrue(b.getMass() >= 1 && b.getMass() < 10, b::toString);
        }
    }

    @Test
    void sameSeedGivesTheSameSet() {
        final List<Body> first = BodySets.random(N_BODIES, SEED);
        final List<Body> second = BodySets.random(N_BODIES, SEED);
        for (int i = 0; i < N_BODIES; i++) {
            assertEquals(first.get(i).getPos(), second.get(i).getPos());
            assertEquals(first.get(i).getVel(), second.get(i).getVel());
        }
    }

    @Test
    void differentSeedsGiveDifferentSets() {
        assertNotEquals(BodySets.random(1, SEED).get(0).getPos(), BodySets.random(1, SEED + 1).get(0).getPos());
    }

    @Test
    void setsAreUnmodifiable() {
        final List<Body> bodies = BodySets.random(1, SEED);
        assertThrows(UnsupportedOperationException.class, bodies::clear);
    }
}
