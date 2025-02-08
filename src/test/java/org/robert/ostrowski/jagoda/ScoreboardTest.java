package org.robert.ostrowski.jagoda;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

//Todo: Add more multithreading tests
public class ScoreboardTest {

    String homeName = "Home";
    String awayName = "Away";
    String homeName2 = "Home2";
    String awayName2 = "Away2";

    @Test
    void testGetMatchImmutability() {
        int homeTeamScore1 = 1234;
        int awayTeamScore1 = 5678;
        int homeTeamScore2 = 4321;
        int awayTeamScore2 = 8765;
        Scoreboard scoreboard = new Scoreboard();

        long id = scoreboard.startNewMatch(homeName, awayName);

        scoreboard.updateScore(id, homeTeamScore1, awayTeamScore1);

        Match match1 = scoreboard.getMatchById(id);
        Assertions.assertEquals(homeTeamScore1, match1.getHomeTeamScore());
        Assertions.assertEquals(awayTeamScore1, match1.getAwayTeamScore());

        scoreboard.updateScore(id, homeTeamScore2, awayTeamScore2);

        Match match2 = scoreboard.getMatchById(id);

        Assertions.assertEquals(homeTeamScore2, match2.getHomeTeamScore());
        Assertions.assertEquals(awayTeamScore2, match2.getAwayTeamScore());
        Assertions.assertEquals(homeTeamScore1, match1.getHomeTeamScore());
        Assertions.assertEquals(awayTeamScore1, match1.getAwayTeamScore());
    }

    @Test
    void testGetSummaryImmutability() {
        int homeTeamScore1 = 1234;
        int awayTeamScore1 = 5678;
        int homeTeamScore2 = 4321;
        int awayTeamScore2 = 8765;
        Scoreboard scoreboard = new Scoreboard();

        long id = scoreboard.startNewMatch(homeName, awayName);

        scoreboard.updateScore(id, homeTeamScore1, awayTeamScore1);

        Match match1a = scoreboard.getSummary().get(0);
        Assertions.assertEquals(homeTeamScore1, match1a.getHomeTeamScore());
        Assertions.assertEquals(awayTeamScore1, match1a.getAwayTeamScore());

        scoreboard.updateScore(id, homeTeamScore2, awayTeamScore2);

        Match match2a = scoreboard.getSummary().get(0);

        Assertions.assertEquals(homeTeamScore2, match2a.getHomeTeamScore());
        Assertions.assertEquals(awayTeamScore2, match2a.getAwayTeamScore());
        Assertions.assertEquals(homeTeamScore1, match1a.getHomeTeamScore());
        Assertions.assertEquals(awayTeamScore1, match1a.getAwayTeamScore());
    }

    @Test
    void testStartAndGetByIdForDefaultMatch() {
        Scoreboard scoreboard = new Scoreboard();
        long matchId = scoreboard.startNewMatch(homeName, awayName);
        Match match = scoreboard.getMatchById(matchId);
        Assertions.assertEquals(new Match(homeName, awayName), match);
    }

    @Test
    void testStartAndSummaryForMultipleDefaultMatches() {
        Scoreboard scoreboard = new Scoreboard();

        scoreboard.startNewMatch(homeName, awayName);
        Assertions.assertEquals(1, scoreboard.getSummary().size());

        scoreboard.startNewMatch(homeName2, awayName2);

        List<Match> matches = scoreboard.getSummary();
        Assertions.assertEquals(2, matches.size());

        Assertions.assertEquals(new Match(homeName2, awayName2), matches.get(0));
        Assertions.assertEquals(new Match(homeName, awayName), matches.get(1));
    }

    @Test
    void testMultipleStartsWithSameHomeTeamName() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startNewMatch(homeName, awayName);
        Assertions.assertThrows(IllegalStateException.class, () -> scoreboard.startNewMatch(homeName, "Away2"));
    }

    @Test
    void testMultipleStartsWithSameAwayTeamName() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.startNewMatch(homeName, awayName);
        Assertions.assertThrows(IllegalStateException.class, () -> scoreboard.startNewMatch("Home2", awayName));
    }

    @Test
    void testStartFinishAndSummaryForDefaultMatch() {
        Scoreboard scoreboard = new Scoreboard();

        long matchId = scoreboard.startNewMatch(homeName, awayName);
        Assertions.assertEquals(1, scoreboard.getSummary().size());

        scoreboard.finishMatch(matchId);
        Assertions.assertEquals(0, scoreboard.getSummary().size());

        Assertions.assertDoesNotThrow(() -> scoreboard.startNewMatch(homeName, awayName));
    }

    @Test
    void testFinishWithInvalidMatchId() {
        Scoreboard scoreboard = new Scoreboard();
        Assertions.assertThrows(IllegalArgumentException.class, () -> scoreboard.finishMatch(12));
    }

    @Test
    void testUpdateWithInvalidMatchId() {
        Scoreboard scoreboard = new Scoreboard();
        Assertions.assertThrows(IllegalArgumentException.class, () -> scoreboard.updateScore(12, 0, 0));
    }

    @Test
    void testUpdateAndGetWithValidMatchId() {
        Scoreboard scoreboard = new Scoreboard();

        long matchId = scoreboard.startNewMatch(homeName, awayName);
        Match match = scoreboard.getMatchById(matchId);
        Assertions.assertEquals(new Match(homeName, awayName), match);

        scoreboard.updateScore(matchId, 1234, 5678);

        match = scoreboard.getMatchById(matchId);
        Assertions.assertEquals(1234, match.getHomeTeamScore());
        Assertions.assertEquals(5678, match.getAwayTeamScore());
    }

    @Test
    void testFindMatchId() {
        Scoreboard scoreboard = new Scoreboard();

        long matchId1 = scoreboard.startNewMatch(homeName, awayName);
        long matchId2 = scoreboard.startNewMatch(homeName2, awayName2);

        Assertions.assertEquals(matchId1, scoreboard.findMatchId(homeName));
        Assertions.assertEquals(matchId1, scoreboard.findMatchId(awayName));
        Assertions.assertEquals(matchId2, scoreboard.findMatchId(homeName2));
        Assertions.assertEquals(matchId2, scoreboard.findMatchId(awayName2));

        scoreboard.finishMatch(matchId1);
        Assertions.assertEquals(-1, scoreboard.findMatchId(homeName));
        Assertions.assertEquals(-1, scoreboard.findMatchId(awayName));
    }

    @Test
    void testGetMatchById() {
        Scoreboard scoreboard = new Scoreboard();

        long matchId = scoreboard.startNewMatch(homeName, awayName);

        Match match = scoreboard.getMatchById(matchId);
        Assertions.assertEquals(new Match(homeName, awayName), match);

        scoreboard.finishMatch(matchId);
        Assertions.assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatchById(matchId));
    }

    @Test
    void testGetSummary() {
        Scoreboard scoreboard = new Scoreboard();
        Assertions.assertEquals(0, scoreboard.getSummary().size());

        long matchId1 = scoreboard.startNewMatch(homeName, awayName);
        List<Match> summary = scoreboard.getSummary();
        Assertions.assertEquals(1, summary.size());
        Assertions.assertEquals(new Match(homeName, awayName), summary.get(0));

        scoreboard.startNewMatch(homeName2, awayName2);
        summary = scoreboard.getSummary();
        Assertions.assertEquals(2, summary.size());
        Assertions.assertEquals(new Match(homeName2, awayName2), summary.get(0));

        scoreboard.updateScore(matchId1, 1, 0);
        summary = scoreboard.getSummary();
        Match match1 = scoreboard.getMatchById(matchId1);
        Assertions.assertEquals(2, summary.size());
        Assertions.assertEquals(match1, summary.get(0));
    }

    void multithreadingChange(Scoreboard scoreboard, AtomicLong matchId, String homeName, String awayName) {
        synchronized (matchId) {
            if (matchId.get() == -1) {
                long id = scoreboard.startNewMatch(homeName, awayName);
                matchId.set(id);
            } else {
                scoreboard.finishMatch(matchId.get());
                matchId.set(scoreboard.findMatchId(homeName));
            }
        }
    }

    boolean multithreadingVerify(Scoreboard scoreboard, AtomicLong matchId, AtomicBoolean failed, String homeName, String awayName) {
        synchronized (matchId) {
            if (matchId.get() == -1) {
                if (scoreboard.findMatchId(homeName) != -1) {
                    failed.set(true);
                    return true;
                }
            } else {
                try {
                    scoreboard.getMatchById(matchId.get());
                } catch (IllegalArgumentException exception) {
                    failed.set(true);
                    return true;
                }
            }
        }
        return false;
    }

    void multithreadingLoop(int iterations, AtomicLong match1Id, AtomicLong match2Id, Scoreboard scoreboard, AtomicBoolean failed) {
        try {
            for (int i = 0; i < iterations; i++) {
                multithreadingChange(scoreboard, match1Id, homeName, awayName);
                if (multithreadingVerify(scoreboard, match1Id, failed, homeName, awayName)) return;
                multithreadingChange(scoreboard, match2Id, homeName2, awayName2);
                if (multithreadingVerify(scoreboard, match2Id, failed, homeName2, awayName2)) return;
            }
        } catch (Exception exception) {
            failed.set(true);
            throw exception;
        }
    }

    @Test
    void testMultithreading() throws InterruptedException {
        AtomicLong match1Id = new AtomicLong(-1);
        AtomicLong match2Id = new AtomicLong(-1);
        Scoreboard scoreboard = new Scoreboard();
        AtomicBoolean failed = new AtomicBoolean();

        Thread worker1 = new Thread(() -> multithreadingLoop(1000, match1Id, match2Id, scoreboard, failed));
        Thread worker2 = new Thread(() -> multithreadingLoop(1000, match1Id, match2Id, scoreboard, failed));
        Thread worker3 = new Thread(() -> multithreadingLoop(1000, match1Id, match2Id, scoreboard, failed));

        worker1.start();
        worker2.start();
        worker3.start();
        worker1.join();
        worker2.join();
        worker3.join();

        Assertions.assertFalse(failed.get());
    }
}
