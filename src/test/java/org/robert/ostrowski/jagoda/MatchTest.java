package org.robert.ostrowski.jagoda;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MatchTest {

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
