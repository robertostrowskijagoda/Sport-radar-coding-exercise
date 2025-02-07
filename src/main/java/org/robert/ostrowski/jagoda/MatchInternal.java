package org.robert.ostrowski.jagoda;

import lombok.Getter;

import java.time.Clock;
import java.time.Instant;

@Getter
final class MatchInternal extends Match {

    private String stringId;

    MatchInternal(String homeTeamName, String awayTeamName) {
        super(homeTeamName, awayTeamName);
    }

    void setHomeTeamScore(int score) {
        if (score < 0)
            throw new IllegalArgumentException("Home team score can't be negative");
        homeTeamScore = score;
    }

    void setAwayTeamScore(int score) {
        if (score < 0)
            throw new IllegalArgumentException("Away team score can't be negative");
        awayTeamScore = score;
    }

    int getTotalScore() {
        return homeTeamScore + awayTeamScore;
    }

    public String getStringId() {
        if (stringId == null)
            stringId = generateStringId(homeTeamName, awayTeamName);
        return stringId;
    }

    static String generateStringId(String homeName, String awayName) {
        return homeName + ":" + awayName;
    }
}
