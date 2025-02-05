package org.robert.ostrowski.jagoda;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MatchTest {

    @Test
    void testCopyConstructor() {
        Match match = new Match("Home", "Away");
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
        String homeName = "Home";
        String awayName = "Away";
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
        String homeName = "Home";
        String awayName = "Away";
        Assertions.assertEquals(homeName + " 0 - " + awayName + " 0", new Match(homeName, awayName).toString());
    }
}
