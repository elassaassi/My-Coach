package org.elas.momentum.rating.application.usecase;

import org.elas.momentum.rating.domain.event.ManOfMatchEvent;
import org.elas.momentum.rating.RatingSubmittedEvent;
import org.elas.momentum.rating.domain.exception.AlreadyRatedException;
import org.elas.momentum.rating.domain.model.PlayerRating;
import org.elas.momentum.rating.domain.model.PlayerStats;
import org.elas.momentum.rating.domain.port.in.RatePlayerUseCase;
import org.elas.momentum.rating.domain.port.out.PlayerStatsRepository;
import org.elas.momentum.rating.domain.port.out.RatingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatePlayerService implements RatePlayerUseCase {

    private final RatingRepository ratingRepository;
    private final PlayerStatsRepository statsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RatePlayerService(RatingRepository ratingRepository,
                              PlayerStatsRepository statsRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.ratingRepository = ratingRepository;
        this.statsRepository = statsRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String rate(Command command) {
        if (ratingRepository.existsByActivityIdAndRaterIdAndRatedUserId(
                command.activityId(), command.raterId(), command.ratedUserId())) {
            throw new AlreadyRatedException(command.raterId(), command.ratedUserId());
        }

        var rating = PlayerRating.create(
                command.activityId(), command.raterId(), command.ratedUserId(),
                command.behavior(), command.technicality(), command.teamwork(),
                command.level(), command.isManOfMatch(), command.comment()
        );

        ratingRepository.save(rating);
        updateStats(rating);

        int motmVotes = ratingRepository.countManOfMatchVotes(command.activityId(), command.ratedUserId());
        int total = ratingRepository.countActivityParticipants(command.activityId());
        if (total > 1 && motmVotes > total / 2) {
            eventPublisher.publishEvent(ManOfMatchEvent.of(command.activityId(), command.ratedUserId()));
        }

        var stats = statsRepository.findByUserIdAndSport(command.ratedUserId(), "all")
                .orElse(PlayerStats.empty(command.ratedUserId(), "all"));
        eventPublisher.publishEvent(RatingSubmittedEvent.of(
                rating.getId().value(), command.activityId(), command.ratedUserId(), stats.proScore()));

        return rating.getId().value();
    }

    private void updateStats(PlayerRating rating) {
        var existing = statsRepository
                .findByUserIdAndSport(rating.getRatedUserId(), "all")
                .orElse(PlayerStats.empty(rating.getRatedUserId(), "all"));

        int newTotal = existing.totalRatings() + 1;
        double newBehavior = ((existing.avgBehavior() * existing.totalRatings()) + rating.getBehavior()) / newTotal;
        double newTechnicality = ((existing.avgTechnicality() * existing.totalRatings()) + rating.getTechnicality()) / newTotal;
        double newTeamwork = ((existing.avgTeamwork() * existing.totalRatings()) + rating.getTeamwork()) / newTotal;
        int newMotm = existing.manOfMatchCount() + (rating.isManOfMatch() ? 1 : 0);

        statsRepository.save(existing.withUpdatedRating(newBehavior, newTechnicality, newTeamwork, newTotal, newMotm));
    }
}
