package org.robert.ostrowski.jagoda;

import lombok.Getter;

@Getter
public class Match {
    protected final String homeTeamName;
    protected final String awayTeamName;
    protected volatile int homeTeamScore;
    protected volatile int awayTeamScore;

    Match(String homeTeamName, String awayTeamName) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(homeTeamName);
        stringBuilder.append(' ');
        stringBuilder.append(homeTeamScore);
        stringBuilder.append(" - ");
        stringBuilder.append(awayTeamName);
        stringBuilder.append(' ');
        stringBuilder.append(awayTeamScore);
        return stringBuilder.toString();
    }
}
