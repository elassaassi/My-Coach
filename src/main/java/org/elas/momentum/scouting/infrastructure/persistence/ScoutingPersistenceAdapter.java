package org.elas.momentum.scouting.infrastructure.persistence;

import org.elas.momentum.scouting.domain.model.RecruiterProfile;
import org.elas.momentum.scouting.domain.model.ScoutingInterest;
import org.elas.momentum.scouting.domain.model.TalentProfile;
import org.elas.momentum.scouting.domain.port.out.RecruiterRepository;
import org.elas.momentum.scouting.domain.port.out.ScoutingInterestRepository;
import org.elas.momentum.scouting.domain.port.out.TalentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class ScoutingPersistenceAdapter implements TalentRepository, RecruiterRepository, ScoutingInterestRepository {

    private final TalentJpaRepository talentJpaRepository;
    private final RecruiterJpaRepository recruiterJpaRepository;
    private final ScoutingInterestJpaRepository interestJpaRepository;

    ScoutingPersistenceAdapter(TalentJpaRepository talentJpaRepository,
                               RecruiterJpaRepository recruiterJpaRepository,
                               ScoutingInterestJpaRepository interestJpaRepository) {
        this.talentJpaRepository = talentJpaRepository;
        this.recruiterJpaRepository = recruiterJpaRepository;
        this.interestJpaRepository = interestJpaRepository;
    }

    // ── TalentRepository ──────────────────────────────────────────────────────

    @Override
    public TalentProfile save(TalentProfile talent) {
        return ScoutingMapper.toDomain(
                talentJpaRepository.save(ScoutingMapper.toEntity(talent)));
    }

    @Override
    public Optional<TalentProfile> findByUserId(String userId) {
        return talentJpaRepository.findByUserId(userId).map(ScoutingMapper::toDomain);
    }

    @Override
    public List<TalentProfile> findBySportAndMinScore(String sport, int minScore) {
        return talentJpaRepository.findBySportAndMinScore(sport, minScore).stream()
                .map(ScoutingMapper::toDomain)
                .toList();
    }

    // ── RecruiterRepository ───────────────────────────────────────────────────

    @Override
    public RecruiterProfile save(RecruiterProfile recruiter) {
        return ScoutingMapper.toDomain(
                recruiterJpaRepository.save(ScoutingMapper.toEntity(recruiter)));
    }

    @Override
    public Optional<RecruiterProfile> findById(String id) {
        return recruiterJpaRepository.findById(id).map(ScoutingMapper::toDomain);
    }

    @Override
    public Optional<RecruiterProfile> findRecruiterByUserId(String userId) {
        return recruiterJpaRepository.findByUserId(userId).map(ScoutingMapper::toDomain);
    }

    // ── ScoutingInterestRepository ────────────────────────────────────────────

    @Override
    public ScoutingInterest save(ScoutingInterest interest) {
        return ScoutingMapper.toDomain(
                interestJpaRepository.save(ScoutingMapper.toEntity(interest)));
    }

    @Override
    public List<ScoutingInterest> findByRecruiterId(String recruiterId) {
        return interestJpaRepository.findByRecruiterId(recruiterId).stream()
                .map(ScoutingMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByRecruiterIdAndTalentId(String recruiterId, String talentId) {
        return interestJpaRepository.existsByRecruiterIdAndTalentId(recruiterId, talentId);
    }
}
