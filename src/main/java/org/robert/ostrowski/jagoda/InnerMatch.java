package org.robert.ostrowski.jagoda;

import lombok.Getter;

import java.time.Clock;
import java.time.Instant;

@Getter
final class InnerMatch extends Match {

    private final Instant timestamp;
    private String stringId;

    InnerMatch(Clock clock, String homeTeamName, String awayTeamName) {
        super(homeTeamName, awayTeamName);
        timestamp = Instant.now(clock);
    }

    void setHomeTeamScore(int score) {
        homeTeamScore = score;
    }

    void setAwayTeamScore(int score) {
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
