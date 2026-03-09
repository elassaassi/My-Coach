package org.elas.momentum.scouting.application.usecase;

import org.elas.momentum.scouting.domain.model.TalentProfile;
import org.elas.momentum.scouting.domain.port.in.SearchTalentsUseCase;
import org.elas.momentum.scouting.domain.port.out.TalentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
public class SearchTalentsService implements SearchTalentsUseCase {

    private final TalentRepository talentRepository;

    public SearchTalentsService(TalentRepository talentRepository) {
        this.talentRepository = talentRepository;
    }

    @Override
    public List<TalentProfile> search(Query query) {
        int minScore = query.minProScore() != null ? query.minProScore() : 0;

        // Support comma-separated sports for multi-sport search
        if (query.sport() != null && query.sport().contains(",")) {
            String[] sports = query.sport().split(",");
            List<CompletableFuture<List<TalentProfile>>> futures = Arrays.stream(sports)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(sport -> CompletableFuture.supplyAsync(
                            () -> talentRepository.findBySportAndMinScore(sport, minScore)))
                    .toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        List<TalentProfile> combined = new ArrayList<>();
                        futures.forEach(f -> combined.addAll(f.join()));
                        return combined;
                    })
                    .join();
        }

        return talentRepository.findBySportAndMinScore(
                query.sport() != null ? query.sport() : "", minScore);
    }
}
