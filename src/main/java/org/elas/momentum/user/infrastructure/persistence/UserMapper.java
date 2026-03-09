package org.elas.momentum.user.infrastructure.persistence;

import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.model.*;

import java.util.List;

public final class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserEntity entity) {
        List<SportLevel> sportLevels = entity.getSportLevels().stream()
                .map(sl -> new SportLevel(
                        sl.getSport(),
                        Proficiency.valueOf(sl.getProficiency()),
                        sl.getYearsExperience()))
                .toList();

        double lat = entity.getLatitude() != null ? entity.getLatitude() : 0.0;
        double lon = entity.getLongitude() != null ? entity.getLongitude() : 0.0;
        var sportProfile = new SportProfile(
                sportLevels, lat, lon,
                entity.getCity() != null ? entity.getCity() : "",
                entity.getCountry() != null ? entity.getCountry() : "");

        return User.reconstitute(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAvatarUrl(),
                sportProfile,
                UserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static UserEntity toEntity(User user) {
        var entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setAvatarUrl(user.getAvatarUrl());
        entity.setStatus(user.getStatus().name());
        entity.setLatitude(user.getSportProfile().latitude());
        entity.setLongitude(user.getSportProfile().longitude());
        entity.setCity(user.getSportProfile().city());
        entity.setCountry(user.getSportProfile().country());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        List<SportLevelEntity> sportLevelEntities = user.getSportProfile().sports().stream()
                .map(sl -> {
                    var sle = new SportLevelEntity();
                    sle.setSport(sl.sport());
                    sle.setProficiency(sl.proficiency().name());
                    sle.setYearsExperience(sl.yearsExperience());
                    sle.setUser(entity);
                    return sle;
                })
                .toList();

        entity.setSportLevels(new java.util.ArrayList<>(sportLevelEntities));
        return entity;
    }

    public static UserResult toResult(User user) {
        var sportLevels = user.getSportProfile().sports().stream()
                .map(sl -> new UserResult.SportLevelDto(sl.sport(), sl.proficiency().name(), sl.yearsExperience()))
                .toList();

        var sportProfile = new UserResult.SportProfileDto(
                sportLevels,
                user.getSportProfile().latitude(),
                user.getSportProfile().longitude(),
                user.getSportProfile().city(),
                user.getSportProfile().country()
        );

        return new UserResult(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatarUrl(),
                user.getStatus().name(),
                sportProfile,
                user.getCreatedAt()
        );
    }
}
