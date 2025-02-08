package org.robert.ostrowski.jagoda;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Match {
    protected final String homeTeamName;
    protected final String awayTeamName;
    protected volatile int homeTeamScore;
    protected volatile int awayTeamScore;

    protected Match(Match other) {
        homeTeamName = other.homeTeamName;
        awayTeamName = other.awayTeamName;
        homeTeamScore = other.homeTeamScore;
        awayTeamScore = other.awayTeamScore;
    }

    protected Match(String homeTeamName, String awayTeamName) {
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

    protected void setHomeTeamScore(int score) {
        if (score < 0)
            throw new IllegalArgumentException("Home team score can't be negative");
        homeTeamScore = score;
    }

    protected void setAwayTeamScore(int score) {
        if (score < 0)
            throw new IllegalArgumentException("Away team score can't be negative");
        awayTeamScore = score;
    }

    protected int getTotalScore() {
        return homeTeamScore + awayTeamScore;
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
