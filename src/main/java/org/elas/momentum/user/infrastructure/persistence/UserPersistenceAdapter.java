package org.elas.momentum.user.infrastructure.persistence;

import org.elas.momentum.user.domain.model.Email;
import org.elas.momentum.user.domain.model.User;
import org.elas.momentum.user.domain.model.UserId;
import org.elas.momentum.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        var found = jpaRepository.findById(user.getId().value());
        if (found.isPresent()) {
            UserEntity existing = found.get();
            mergeBasicFields(existing, user);
            // Flush the DELETE first — avoids unique constraint violation on (user_id, sport)
            existing.getSportLevels().clear();
            jpaRepository.saveAndFlush(existing);
            // Now INSERT the new sports
            user.getSportProfile().sports().forEach(sl -> {
                var sle = new SportLevelEntity();
                sle.setSport(sl.sport());
                sle.setProficiency(sl.proficiency().name());
                sle.setYearsExperience(sl.yearsExperience());
                sle.setUser(existing);
                existing.getSportLevels().add(sle);
            });
            return UserMapper.toDomain(jpaRepository.save(existing));
        }
        return UserMapper.toDomain(jpaRepository.save(UserMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(UserMapper::toDomain);
    }

    @Override
    public Map<String, User> findByIds(Collection<UserId> ids) {
        var rawIds = ids.stream().map(UserId::value).toList();
        return jpaRepository.findAllByIdIn(rawIds).stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toMap(u -> u.getId().value(), u -> u));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public List<User> findBySport(String sport, String excludeUserId) {
        return jpaRepository.findBySportAndExclude(sport, excludeUserId)
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    private void mergeBasicFields(UserEntity existing, User user) {
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setAvatarUrl(user.getAvatarUrl());
        existing.setStatus(user.getStatus().name());
        existing.setLatitude(user.getSportProfile().latitude());
        existing.setLongitude(user.getSportProfile().longitude());
        existing.setCity(user.getSportProfile().city());
        existing.setCountry(user.getSportProfile().country());
        existing.setUpdatedAt(user.getUpdatedAt());
    }
}
