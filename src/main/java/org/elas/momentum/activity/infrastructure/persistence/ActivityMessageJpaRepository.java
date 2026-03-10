package org.elas.momentum.activity.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityMessageJpaRepository extends JpaRepository<ActivityMessageEntity, String> {

    List<ActivityMessageEntity> findByActivityIdOrderBySentAtAsc(String activityId, Pageable pageable);
}
