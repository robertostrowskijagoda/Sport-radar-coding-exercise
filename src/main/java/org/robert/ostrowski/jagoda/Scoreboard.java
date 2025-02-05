package org.robert.ostrowski.jagoda;

import java.time.Clock;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public final class Scoreboard {

    final Clock clock = Clock.system(ZoneId.of("UTC"));
    final ConcurrentMap<Long, MatchInternal> matches = new ConcurrentHashMap<>();
    final ConcurrentMap<String, Long> matchesKeys = new ConcurrentHashMap<>();
    final ConcurrentMap<String, Long> activeTeams = new ConcurrentHashMap<>();
    final ConcurrentLinkedQueue<Long> freeKeys = new ConcurrentLinkedQueue<>();
    final AtomicLong currKey = new AtomicLong();

    public long startNewMatch(String homeTeamName, String awayTeamName) {
        long key = getFreeKey();

        Long existingHome = activeTeams.putIfAbsent(homeTeamName, key);
        if (existingHome != null) {
            returnKey(key);
            throw new IllegalStateException("Home team is during match (id: " + existingHome + ")");
        }

        try {
            Long existingAway = activeTeams.putIfAbsent(awayTeamName, key);
            if (existingAway != null) {
                activeTeams.remove(homeTeamName, key);
                returnKey(key);
                throw new IllegalStateException("Away team is during match (id: " + existingAway + ")");
            }

            MatchInternal matchInternal = new MatchInternal(clock, homeTeamName, awayTeamName);
            matches.put(key, matchInternal);
            matchesKeys.put(matchInternal.getStringId(), key);
            return key;
        } catch (Throwable t) {
            activeTeams.remove(homeTeamName, key);
            activeTeams.remove(awayTeamName, key);
            returnKey(key);
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
        returnKey(matchId);
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
        return matches.values().parallelStream().sorted((m1, m2) -> {
            int diff = m2.getTotalScore() - m1.getTotalScore();
            if (diff != 0)
                return diff;
            return m2.getTimestamp().compareTo(m1.getTimestamp());
        }).map(Match::new).toList();
    }

    private long getFreeKey() {
        Long key = freeKeys.poll();
        return key != null ? key : currKey.getAndIncrement();
    }

    private void returnKey(long key) {
        freeKeys.add(key);
    }

    private MatchInternal getMatchOrThrow(long matchId) {
        MatchInternal match = matches.get(matchId);
        if (match == null)
            throw new IllegalArgumentException("Provided match id do not correspond with any match");
        return match;
    }
}
