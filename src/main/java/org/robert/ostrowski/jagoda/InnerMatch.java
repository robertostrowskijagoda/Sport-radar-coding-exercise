package org.robert.ostrowski.jagoda;

import lombok.Getter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Instant;

class InnerMatch extends Match {

    @Getter
    private final Instant timestamp;

    public InnerMatch(String homeTeamName, String awayTeamName, int homeTeamScore, int awayTeamScore) {
        super(homeTeamName, awayTeamName, homeTeamScore, awayTeamScore);
        throw new NotImplementedException();
    }

    public int getTotalScore() {
        throw new NotImplementedException();
    }

    public String getStringId() {
        throw new NotImplementedException();
    }
}
