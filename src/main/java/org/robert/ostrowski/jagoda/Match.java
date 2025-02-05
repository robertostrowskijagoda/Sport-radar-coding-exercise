package org.robert.ostrowski.jagoda;

import lombok.Getter;

@Getter
public class Match {
    protected final String homeTeamName;
    protected final String awayTeamName;
    protected volatile int homeTeamScore;
    protected volatile int awayTeamScore;

    Match(String homeTeamName, String awayTeamName) {
        if (homeTeamName == null || homeTeamName.isEmpty())
            throw new IllegalArgumentException("Home team name can't be null or empty");
        if (awayTeamName == null || awayTeamName.isEmpty())
            throw new IllegalArgumentException("Away team name can't be null or empty");
        if (homeTeamName.equals(awayTeamName))
            throw new IllegalArgumentException("Home team name can't be the same as away team name");
        if (homeTeamName.contains(":"))
            throw new IllegalArgumentException("Home team name can't contain ':' special character");
        if (awayTeamName.contains(":"))
            throw new IllegalArgumentException("Away team name can't contain ':' special character");
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
