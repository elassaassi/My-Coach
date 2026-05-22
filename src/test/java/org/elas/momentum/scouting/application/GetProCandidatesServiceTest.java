package org.elas.momentum.scouting.application;

import org.elas.momentum.scouting.application.usecase.GetProCandidatesService;
import org.elas.momentum.scouting.domain.model.TalentProfile;
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
class GetProCandidatesServiceTest {

    @Mock TalentRepository talentRepository;

    GetProCandidatesService service;

    @BeforeEach
    void setUp() {
        service = new GetProCandidatesService(talentRepository);
    }

    @Test
    @DisplayName("getProCandidates() délègue au repository avec sport et score minimum")
    void getProCandidates_delegatesToRepo() {
        var talent = mock(TalentProfile.class);
        when(talentRepository.findBySportAndMinScore("football", 80)).thenReturn(List.of(talent));

        var result = service.getProCandidates("football", 80);

        assertThat(result).containsExactly(talent);
        verify(talentRepository).findBySportAndMinScore("football", 80);
    }

    @Test
    @DisplayName("getProCandidates() aucun résultat — retourne liste vide")
    void getProCandidates_noResults_returnsEmpty() {
        when(talentRepository.findBySportAndMinScore(any(), anyInt())).thenReturn(List.of());

        assertThat(service.getProCandidates("tennis", 90)).isEmpty();
    }
}