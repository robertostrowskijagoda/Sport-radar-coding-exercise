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

public class InnerMatchTest {

    String homeName = "Home";
    String awayName = "Away";
    Clock defaultClock = Clock.systemUTC();

    @Test
    void testProperValuesPassedToBase() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        Assertions.assertEquals(homeName, innerMatch.getHomeTeamName());
        Assertions.assertEquals(awayName, innerMatch.getAwayTeamName());
        Assertions.assertEquals(0, innerMatch.getHomeTeamScore());
        Assertions.assertEquals(0, innerMatch.getAwayTeamScore());
    }

    @Test
    void testProperTimestamp() {
        Clock clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("UTC"));
        InnerMatch innerMatch = new InnerMatch(clock, homeName, awayName);
        Assertions.assertEquals(clock.instant(), innerMatch.getTimestamp());
    }

    @Test
    void testGetStringId() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        Assertions.assertEquals(homeName + ":" + awayName, innerMatch.getStringId());
    }

    void testSetNegativeScore(Consumer<Integer> setter) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> setter.accept(-1));
    }

    @Test
    void testSetHomeNegativeScore() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        testSetNegativeScore(innerMatch::setHomeTeamScore);
    }

    @Test
    void testSetAwayNegativeScore() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        testSetNegativeScore(innerMatch::setAwayTeamScore);
    }

    void testSingleThreadSetGet(Consumer<Integer> setter, Supplier<Integer> getter) {
        int score = 1234;
        setter.accept(score);
        Assertions.assertEquals(score, getter.get());
    }

    @Test
    void testSingleThreadSetGetHomeScore() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        testSingleThreadSetGet(innerMatch::setHomeTeamScore, innerMatch::getHomeTeamScore);
    }

    @Test
    void testSingleThreadSetGetAwayScore() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        testSingleThreadSetGet(innerMatch::setAwayTeamScore, innerMatch::getAwayTeamScore);
    }

    @Test
    void testSingleThreadGetTotalScore() {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        Assertions.assertEquals(0, innerMatch.getTotalScore());
        innerMatch.setAwayTeamScore(5);
        Assertions.assertEquals(5, innerMatch.getTotalScore());
        innerMatch.setHomeTeamScore(10);
        Assertions.assertEquals(15, innerMatch.getTotalScore());
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
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        testMultithreadedSetGetScore(innerMatch::setHomeTeamScore, innerMatch::getHomeTeamScore);
    }

    @Test
    void testMultithreadedSetGetAwayScore() throws InterruptedException {
        InnerMatch innerMatch = new InnerMatch(defaultClock, homeName, awayName);
        testMultithreadedSetGetScore(innerMatch::setAwayTeamScore, innerMatch::getAwayTeamScore);
    }
}
