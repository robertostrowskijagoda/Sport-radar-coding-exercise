package org.robert.ostrowski.jagoda;

import lombok.Getter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Getter
public class Match {
    protected final String homeTeamName;
    protected final String awayTeamName;
    protected volatile int homeTeamScore;
    protected volatile int awayTeamScore;

    Match(String homeTeamName, String awayTeamName) {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        throw new NotImplementedException();
    }
}
