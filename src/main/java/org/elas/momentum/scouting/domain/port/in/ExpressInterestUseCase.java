package org.elas.momentum.scouting.domain.port.in;

public interface ExpressInterestUseCase {

    record Command(String recruiterId, String talentId, String note) {}

    String express(Command command);
}
