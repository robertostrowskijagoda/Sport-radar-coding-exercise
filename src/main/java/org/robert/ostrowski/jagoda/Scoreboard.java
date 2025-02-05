package org.robert.ostrowski.jagoda;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class Scoreboard {

    public long startNewMatch (String homeTeamName, String awayTeamName) {
        throw new NotImplementedException();
    }

    public void updateScore (long matchId, int homeTeamScore, int awayTeamScore) {
        throw new NotImplementedException();
    }

    public void finishMatch (long matchId) {
        throw new NotImplementedException();
    }

    public long findMatchId (String homeTeamName, String awayTeamName) {
        throw new NotImplementedException();
    }

    public Match getMatchById (long id) {
        throw new NotImplementedException();
    }

    public List<Match> getSummary () {
        throw new NotImplementedException();
    }
}
