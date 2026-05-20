package org.elas.momentum.scouting.application;

import org.elas.momentum.scouting.application.usecase.IndexTalentService;
import org.elas.momentum.scouting.domain.model.TalentProfile;
import org.elas.momentum.scouting.domain.port.out.TalentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndexTalentServiceTest {

    @Mock TalentRepository talentRepository;

    IndexTalentService service;

    @BeforeEach
    void setUp() {
        service = new IndexTalentService(talentRepository);
    }

    @Test
    @DisplayName("index() talent inexistant — crée un nouveau profil")
    void index_notFound_createsNewProfile() {
        when(talentRepository.findByUserId("user-1")).thenReturn(Optional.empty());

        service.index("user-1", "football", 72);

        var captor = ArgumentCaptor.forClass(TalentProfile.class);
        verify(talentRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId().value()).isEqualTo("user-1");
        assertThat(captor.getValue().getProScore()).isEqualTo(72);
    }

    @Test
    @DisplayName("index() talent existant — met à jour le score")
    void index_found_updatesScore() {
        var existing = TalentProfile.create("user-1", "football", 50);
        when(talentRepository.findByUserId("user-1")).thenReturn(Optional.of(existing));

        service.index("user-1", "football", 85);

        var captor = ArgumentCaptor.forClass(TalentProfile.class);
        verify(talentRepository).save(captor.capture());
        assertThat(captor.getValue().getProScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("index() appelle save exactement une fois")
    void index_callsSaveOnce() {
        when(talentRepository.findByUserId(any())).thenReturn(Optional.empty());

        service.index("user-1", "tennis", 60);

        verify(talentRepository, times(1)).save(any());
    }
}