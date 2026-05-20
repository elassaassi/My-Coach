package org.elas.momentum.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserEntity> findAllByIdIn(Collection<String> ids);

    @Query("SELECT DISTINCT u FROM UserEntity u JOIN u.sportLevels sl " +
           "WHERE sl.sport = :sport AND u.id <> :excludeId AND u.status = 'ACTIVE'")
    List<UserEntity> findBySportAndExclude(@Param("sport") String sport,
                                           @Param("excludeId") String excludeId);
}
