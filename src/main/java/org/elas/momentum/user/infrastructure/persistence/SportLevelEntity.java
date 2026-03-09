package org.elas.momentum.user.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "user_sport_levels", schema = "users")
public class SportLevelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "sport", nullable = false)
    private String sport;

    @Column(name = "proficiency", nullable = false)
    private String proficiency;

    @Column(name = "years_experience")
    private int yearsExperience;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }

    public int getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }
}
