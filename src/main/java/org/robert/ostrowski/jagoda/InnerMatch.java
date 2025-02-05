package org.robert.ostrowski.jagoda;

import lombok.Getter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Clock;
import java.time.Instant;

final class InnerMatch extends Match {

    @Getter
    private final Instant timestamp;

    public InnerMatch(Clock clock, String homeTeamName, String awayTeamName) {
        super(homeTeamName, awayTeamName);
        throw new NotImplementedException();
    }

    public void setHomeTeamScore(int score) {
        throw new NotImplementedException();
    }

    public void setAwayTeamScore(int score) {
        throw new NotImplementedException();
    }

    public int getTotalScore() {
        throw new NotImplementedException();
    }

    public String getStringId() {
        throw new NotImplementedException();
    }
}
