package org.elas.momentum.scouting.domain.port.out;

import org.elas.momentum.scouting.domain.model.RecruiterProfile;

import java.util.Optional;

public interface RecruiterRepository {

    RecruiterProfile save(RecruiterProfile recruiter);

    Optional<RecruiterProfile> findById(String id);

    Optional<RecruiterProfile> findRecruiterByUserId(String userId);
}
