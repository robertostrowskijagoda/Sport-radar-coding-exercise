package org.robert.ostrowski.jagoda;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public final class Scoreboard {

    final ConcurrentMap<Long, MatchInternal> matches = new ConcurrentHashMap<>();
    final ConcurrentMap<String, Long> matchesKeys = new ConcurrentHashMap<>();
    final ConcurrentMap<String, Long> activeTeams = new ConcurrentHashMap<>();
    final AtomicLong currKey = new AtomicLong();

    public long startNewMatch(String homeTeamName, String awayTeamName) {
        long key = currKey.getAndIncrement();

        Long existingHome = activeTeams.putIfAbsent(homeTeamName, key);
        if (existingHome != null) {
            throw new IllegalStateException("Home team is during match (id: " + existingHome + ")");
        }

        try {
            Long existingAway = activeTeams.putIfAbsent(awayTeamName, key);
            if (existingAway != null) {
                activeTeams.remove(homeTeamName, key);
                throw new IllegalStateException("Away team is during match (id: " + existingAway + ")");
            }

            MatchInternal matchInternal = new MatchInternal(homeTeamName, awayTeamName);
            matches.put(key, matchInternal);
            matchesKeys.put(matchInternal.getStringId(), key);
            return key;
        } catch (Throwable t) {
            activeTeams.remove(homeTeamName, key);
            activeTeams.remove(awayTeamName, key);
            throw t;
        }
    }

    public void updateScore (long matchId, int homeTeamScore, int awayTeamScore) {
        MatchInternal match = getMatchOrThrow(matchId);
        match.setHomeTeamScore(homeTeamScore);
        match.setAwayTeamScore(awayTeamScore);
    }

    public void finishMatch (long matchId) {
        MatchInternal match = getMatchOrThrow(matchId);
        String stringId = match.getStringId();
        matches.remove(matchId);
        matchesKeys.remove(stringId);
        activeTeams.remove(match.getHomeTeamName(), matchId);
        activeTeams.remove(match.getAwayTeamName(), matchId);
    }

    public long findMatchId (String homeTeamName, String awayTeamName) {
        String stringId = MatchInternal.generateStringId(homeTeamName, awayTeamName);
        Long matchId = matchesKeys.get(stringId);
        if (matchId == null)
            return -1;
        return matchId;
    }

    public Match getMatchById (long matchId) {
        return new Match(getMatchOrThrow(matchId));
    }

    public List<Match> getSummary () {
        return matches.entrySet().parallelStream().sorted((e1, e2) -> {
            MatchInternal m1 = e1.getValue();
            MatchInternal m2 = e2.getValue();
            int diff = m2.getTotalScore() - m1.getTotalScore();
            if (diff != 0)
                return diff;
            long timeDiff = e2.getKey() - e1.getKey();
            return timeDiff < 0 ? -1 : (timeDiff > 0 ? 1 : 0);
        }).map(e -> new Match(e.getValue())).toList();
    }

    private MatchInternal getMatchOrThrow(long matchId) {
        MatchInternal match = matches.get(matchId);
        if (match == null)
            throw new IllegalArgumentException("Provided match id do not correspond with any match");
        return match;
    }
}
