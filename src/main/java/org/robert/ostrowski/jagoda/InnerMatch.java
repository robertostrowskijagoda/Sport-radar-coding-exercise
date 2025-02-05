package org.robert.ostrowski.jagoda;

import lombok.Getter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Clock;
import java.time.Instant;

final class InnerMatch extends Match {

    @Getter
    private final Instant timestamp;

    InnerMatch(Clock clock, String homeTeamName, String awayTeamName) {
        super(homeTeamName, awayTeamName);
        throw new NotImplementedException();
    }

    void setHomeTeamScore(int score) {
        throw new NotImplementedException();
    }

    void setAwayTeamScore(int score) {
        throw new NotImplementedException();
    }

    int getTotalScore() {
        throw new NotImplementedException();
    }

    public String getStringId() {
        throw new NotImplementedException();
    }

    static String generateStringId(String homeName, String awayName) {
        throw new NotImplementedException();
    }
}
