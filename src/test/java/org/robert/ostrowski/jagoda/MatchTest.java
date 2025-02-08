package org.robert.ostrowski.jagoda;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MatchTest {

    String homeName = "Home";
    String awayName = "Away";

    @Test
    void testCopyConstructor() {
        Match match = new Match(homeName, awayName);
        match.awayTeamScore = 1234;
        match.homeTeamScore = 5678;
        Match copyMatch = new Match(match);
        Assertions.assertEquals(match.homeTeamName, copyMatch.homeTeamName);
        Assertions.assertEquals(match.awayTeamName, copyMatch.awayTeamName);
        Assertions.assertEquals(match.homeTeamScore, copyMatch.homeTeamScore);
        Assertions.assertEquals(match.awayTeamScore, copyMatch.awayTeamScore);
    }

    @Test
    void testValidNamesConstructor() {
        MutableObject<Match> matchReference = new MutableObject<>();
        Assertions.assertDoesNotThrow(() -> matchReference.setValue(new Match(homeName, awayName)));
        Match match = matchReference.getValue();
        Assertions.assertEquals(homeName, match.getHomeTeamName());
        Assertions.assertEquals(awayName, match.getAwayTeamName());
        Assertions.assertEquals(0, match.getHomeTeamScore());
        Assertions.assertEquals(0, match.getAwayTeamScore());
    }

    @Test
    void testSameNamesConstructor() {
        String name = "Name";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match(name, name));
    }

    @Test
    void testHomeEmptyNameConstructor() {
        String name = "Name";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match("", name));
    }

    @Test
    void testAwayEmptyNameConstructor() {
        String name = "Name";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match(name, ""));
    }

    @Test
    void testHomeNullNameConstructor() {
        String name = "Name";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match(null, name));
    }

    @Test
    void testAwayNullNameConstructor() {
        String name = "Name";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match(name, null));
    }

    @Test
    void testRestrictedCharInHomeNameConstructor() {
        String homeName = "Home:Name";
        String awayName = "Away";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match(homeName, awayName));
    }

    @Test
    void testRestrictedCharInAwayNameConstructor() {
        String homeName = "Home";
        String awayName = "Away:Name";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Match(homeName, awayName));
    }

    @Test
    void testToString() {
        Assertions.assertEquals(homeName + " 0 - " + awayName + " 0", new Match(homeName, awayName).toString());
    }

    void testSetNegativeScore(Consumer<Integer> setter) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> setter.accept(-1));
    }

    @Test
    void testSetHomeNegativeScore() {
        Match matchInternal = new Match(homeName, awayName);
        testSetNegativeScore(matchInternal::setHomeTeamScore);
    }

    @Test
    void testSetAwayNegativeScore() {
        Match matchInternal = new Match(homeName, awayName);
        testSetNegativeScore(matchInternal::setAwayTeamScore);
    }

    void testSingleThreadSetGet(Consumer<Integer> setter, Supplier<Integer> getter) {
        int score = 1234;
        setter.accept(score);
        Assertions.assertEquals(score, getter.get());
    }

    @Test
    void testSingleThreadSetGetHomeScore() {
        Match matchInternal = new Match(homeName, awayName);
        testSingleThreadSetGet(matchInternal::setHomeTeamScore, matchInternal::getHomeTeamScore);
    }

    @Test
    void testSingleThreadSetGetAwayScore() {
        Match matchInternal = new Match(homeName, awayName);
        testSingleThreadSetGet(matchInternal::setAwayTeamScore, matchInternal::getAwayTeamScore);
    }

    @Test
    void testSingleThreadGetTotalScore() {
        Match matchInternal = new Match(homeName, awayName);
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
        Match matchInternal = new Match(homeName, awayName);
        testMultithreadedSetGetScore(matchInternal::setHomeTeamScore, matchInternal::getHomeTeamScore);
    }

    @Test
    void testMultithreadedSetGetAwayScore() throws InterruptedException {
        Match matchInternal = new Match(homeName, awayName);
        testMultithreadedSetGetScore(matchInternal::setAwayTeamScore, matchInternal::getAwayTeamScore);
    }
}
