package org.robert.ostrowski.jagoda;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScoreboardTest {

    String homeName = "Home";
    String awayName = "Away";
    String homeName2 = "Home2";
    String awayName2 = "Away2";

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
        Assertions.assertEquals(new Match(homeName, awayName), matches.get(0));
        Assertions.assertEquals(new Match(homeName2, awayName2), matches.get(1));
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
        Assertions.assertEquals(1234, match.getHomeTeamScore());
        Assertions.assertEquals(5678, match.getAwayTeamScore());
    }

    @Test
    void testFindMatchId() {
        Scoreboard scoreboard = new Scoreboard();
        long matchId1 = scoreboard.startNewMatch(homeName, awayName);
        long matchId2 = scoreboard.startNewMatch(homeName2, awayName2);
        Assertions.assertEquals(matchId1, scoreboard.findMatchId(homeName, awayName));
        Assertions.assertEquals(matchId2, scoreboard.findMatchId(homeName2, awayName2));
        scoreboard.finishMatch(matchId1);
        Assertions.assertEquals(-1, scoreboard.findMatchId(homeName, awayName));
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
        scoreboard.startNewMatch(homeName, awayName);
        List<Match> summary = scoreboard.getSummary();
        Assertions.assertEquals(1, summary.size());
        Assertions.assertEquals(new Match(homeName, awayName), summary.get(0));
        long matchId2 = scoreboard.startNewMatch(homeName2, awayName2);
        summary = scoreboard.getSummary();
        Assertions.assertEquals(2, summary.size());
        Assertions.assertEquals(new Match(homeName2, awayName2), summary.get(1));
        scoreboard.updateScore(matchId2, 1, 0);
        summary = scoreboard.getSummary();
        Match match2 = scoreboard.getMatchById(matchId2);
        Assertions.assertEquals(2, summary.size());
        Assertions.assertEquals(match2, scoreboard.getMatchById(0));
    }
}
