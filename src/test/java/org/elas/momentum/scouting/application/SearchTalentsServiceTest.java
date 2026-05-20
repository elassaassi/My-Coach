package org.elas.momentum.scouting.application;

import org.elas.momentum.scouting.application.usecase.SearchTalentsService;
import org.elas.momentum.scouting.domain.model.TalentProfile;
import org.elas.momentum.scouting.domain.port.in.SearchTalentsUseCase.Query;
import org.elas.momentum.scouting.domain.port.out.TalentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchTalentsServiceTest {

    @Mock TalentRepository talentRepository;

    SearchTalentsService service;

    @BeforeEach
    void setUp() {
        service = new SearchTalentsService(talentRepository);
    }

    @Test
    @DisplayName("search() sport unique — délègue au repository")
    void search_singleSport_delegatesToRepo() {
        var talent = mock(TalentProfile.class);
        when(talentRepository.findBySportAndMinScore("football", 60)).thenReturn(List.of(talent));

        var result = service.search(new Query("football", 60, null));

        assertThat(result).containsExactly(talent);
    }

    @Test
    @DisplayName("search() minProScore null → utilise 0 par défaut")
    void search_nullMinScore_defaultsToZero() {
        when(talentRepository.findBySportAndMinScore("tennis", 0)).thenReturn(List.of());

        service.search(new Query("tennis", null, null));

        verify(talentRepository).findBySportAndMinScore("tennis", 0);
    }

    @Test
    @DisplayName("search() multi-sports CSV — combine les résultats")
    void search_multiSportCsv_combinesResults() {
        var t1 = mock(TalentProfile.class);
        var t2 = mock(TalentProfile.class);
        when(talentRepository.findBySportAndMinScore("football", 0)).thenReturn(List.of(t1));
        when(talentRepository.findBySportAndMinScore("tennis", 0)).thenReturn(List.of(t2));

        var result = service.search(new Query("football,tennis", null, null));

        assertThat(result).containsExactlyInAnyOrder(t1, t2);
    }

    @Test
    @DisplayName("search() aucun résultat — retourne liste vide")
    void search_noResults_returnsEmpty() {
        when(talentRepository.findBySportAndMinScore(any(), anyInt())).thenReturn(List.of());

        var result = service.search(new Query("badminton", 90, null));

        assertThat(result).isEmpty();
    }
}