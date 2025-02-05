package org.robert.ostrowski.jagoda;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MatchInternalTest {

    String homeName = "Home";
    String awayName = "Away";
    Clock defaultClock = Clock.systemUTC();

    @Test
    void testProperValuesPassedToBase() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        Assertions.assertEquals(homeName, matchInternal.getHomeTeamName());
        Assertions.assertEquals(awayName, matchInternal.getAwayTeamName());
        Assertions.assertEquals(0, matchInternal.getHomeTeamScore());
        Assertions.assertEquals(0, matchInternal.getAwayTeamScore());
    }

    @Test
    void testProperTimestamp() {
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"));
        MatchInternal matchInternal = new MatchInternal(clock, homeName, awayName);
        Assertions.assertEquals(clock.instant(), matchInternal.getTimestamp());
    }

    @Test
    void testGenerateStringId() {
        Assertions.assertEquals(homeName + ":" + awayName, MatchInternal.generateStringId(homeName, awayName));
    }

    @Test
    void testGetStringId() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        Assertions.assertEquals(MatchInternal.generateStringId(homeName, awayName), matchInternal.getStringId());
    }

    void testSetNegativeScore(Consumer<Integer> setter) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> setter.accept(-1));
    }

    @Test
    void testSetHomeNegativeScore() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        testSetNegativeScore(matchInternal::setHomeTeamScore);
    }

    @Test
    void testSetAwayNegativeScore() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        testSetNegativeScore(matchInternal::setAwayTeamScore);
    }

    void testSingleThreadSetGet(Consumer<Integer> setter, Supplier<Integer> getter) {
        int score = 1234;
        setter.accept(score);
        Assertions.assertEquals(score, getter.get());
    }

    @Test
    void testSingleThreadSetGetHomeScore() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        testSingleThreadSetGet(matchInternal::setHomeTeamScore, matchInternal::getHomeTeamScore);
    }

    @Test
    void testSingleThreadSetGetAwayScore() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        testSingleThreadSetGet(matchInternal::setAwayTeamScore, matchInternal::getAwayTeamScore);
    }

    @Test
    void testSingleThreadGetTotalScore() {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        Assertions.assertEquals(0, matchInternal.getTotalScore());
        matchInternal.setAwayTeamScore(5);
        Assertions.assertEquals(5, matchInternal.getTotalScore());
        matchInternal.setHomeTeamScore(10);
        Assertions.assertEquals(15, matchInternal.getTotalScore());
    }

    //I am not sure about this test, but I think that we should test if setter/getter works with multithreading.
    //The idea is to test if getter and setter are operating directly on volatile field or (if not) are synchronized.
    void testMultithreadedSetGetScore(Consumer<Integer> setter, Supplier<Integer> getter) throws InterruptedException {
        Object lock = new Object();
        AtomicInteger value = new AtomicInteger();
        Random random = new Random();
        AtomicBoolean failed = new AtomicBoolean();

        Thread setThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                int v = random.nextInt() & Integer.MAX_VALUE; //This is fast way to make negative values positive, it sets sign bit to 0
                synchronized (lock) {
                    value.set(v);
                    setter.accept(v);
                }
                if (failed.get()) return;
            }
        });

        Thread getThread = new Thread(() -> {
            while (setThread.isAlive()) {
                synchronized (lock) {
                    if (value.get() != getter.get()) {
                        failed.set(true);
                        return;
                    }
                }
            }
        });

        getThread.start();
        setThread.start();
        getThread.join();

        Assertions.assertFalse(failed.get());
    }

    @Test
    void testMultithreadedSetGetHomeScore() throws InterruptedException {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        testMultithreadedSetGetScore(matchInternal::setHomeTeamScore, matchInternal::getHomeTeamScore);
    }

    @Test
    void testMultithreadedSetGetAwayScore() throws InterruptedException {
        MatchInternal matchInternal = new MatchInternal(defaultClock, homeName, awayName);
        testMultithreadedSetGetScore(matchInternal::setAwayTeamScore, matchInternal::getAwayTeamScore);
    }
}
