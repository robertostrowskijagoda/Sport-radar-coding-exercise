package org.robert.ostrowski.jagoda;

import java.time.Clock;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Scoreboard {

    Clock clock = Clock.system(ZoneId.of("UTC"));
    ConcurrentMap<Long, InnerMatch> matches = new ConcurrentHashMap<>();
    ConcurrentMap<String, Long> matchesKeys = new ConcurrentHashMap<>();
    ConcurrentMap<String, Long> activeTeams = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue<Long> freeKeys = new ConcurrentLinkedQueue<>();
    AtomicLong currKey = new AtomicLong();

    public long startNewMatch (String homeTeamName, String awayTeamName) {
        InnerMatch innerMatch = new InnerMatch(clock, homeTeamName, awayTeamName);
        Long homeTeamMatchId = activeTeams.get(homeTeamName);
        if (homeTeamMatchId != null)
            throw new IllegalStateException("Home team is during match (id: " + homeTeamMatchId + ")");
        Long awayTeamMatchId = activeTeams.get(awayTeamName);
        if (awayTeamMatchId != null)
            throw new IllegalStateException("Home team is during match (id: " + awayTeamMatchId + ")");
        long key = getFreeKey();
        matches.put(key, innerMatch);
        matchesKeys.put(innerMatch.getStringId(), key);
        activeTeams.put(homeTeamName, key);
        activeTeams.put(awayTeamName, key);
        return key;
    }

    public void updateScore (long matchId, int homeTeamScore, int awayTeamScore) {
        InnerMatch match = getMatchOrThrow(matchId);
        match.setHomeTeamScore(homeTeamScore);
        match.setAwayTeamScore(awayTeamScore);
    }

    public void finishMatch (long matchId) {
        InnerMatch match = getMatchOrThrow(matchId);
        matchesKeys.remove(match.getStringId());
        matches.remove(matchId);
        activeTeams.remove(match.getHomeTeamName());
        activeTeams.remove(match.getAwayTeamName());
    }

    public long findMatchId (String homeTeamName, String awayTeamName) {
        String stringId = InnerMatch.generateStringId(homeTeamName, awayTeamName);
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
            return m1.getTimestamp().compareTo(m2.getTimestamp());
        }).map(Match::new).toList();
    }

    private long getFreeKey() {
        if (!freeKeys.isEmpty())
            return freeKeys.poll();
        return currKey.getAndIncrement();
    }

    private void returnKey(long key) {
        if (key == currKey.get() - 1)
            currKey.decrementAndGet();
        else
            freeKeys.add(key);
    }

    private InnerMatch getMatchOrThrow(long matchId) {
        InnerMatch match = matches.get(matchId);
        if (match == null)
            throw new IllegalArgumentException("Provided match id do not correspond with any match");
        return match;
    }
}
