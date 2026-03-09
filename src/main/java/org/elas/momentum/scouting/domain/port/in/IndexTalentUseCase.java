package org.elas.momentum.scouting.domain.port.in;

public interface IndexTalentUseCase {

    void index(String userId, String sport, int proScore);
}
