package org.robert.ostrowski.jagoda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@AllArgsConstructor
public class Match {
    private final String homeTeamName;
    private final String awayTeamName;
    @Getter @Setter
    private volatile int homeTeamScore;
    @Getter @Setter
    private volatile int awayTeamScore;

    @Override
    public String toString() {
        throw new NotImplementedException();
    }
}
