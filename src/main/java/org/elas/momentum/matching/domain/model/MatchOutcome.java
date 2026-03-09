package org.elas.momentum.matching.domain.model;

/**
 * Sealed interface — Java 25.
 * Pattern matching exhaustif garanti à la compilation.
 */
public sealed interface MatchOutcome
        permits MatchOutcome.Found, MatchOutcome.NoMatch, MatchOutcome.Pending {

    record Found(String matchedUserId, double score, double distanceKm) implements MatchOutcome {}
    record NoMatch(String reason) implements MatchOutcome {}
    record Pending(String matchRequestId) implements MatchOutcome {}

    static String describe(MatchOutcome outcome) {
        return switch (outcome) {
            case Found(var uid, var score, var dist) ->
                    "Match trouvé : %s (score=%.1f, distance=%.1fkm)".formatted(uid, score, dist);
            case NoMatch(var reason) ->
                    "Pas de match : " + reason;
            case Pending(var id) ->
                    "En attente : " + id;
        };
    }
}
