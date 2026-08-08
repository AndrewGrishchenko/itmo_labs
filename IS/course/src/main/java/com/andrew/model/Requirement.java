package com.andrew.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.andrew.model.enums.RequirementStatus;
import com.andrew.model.enums.SupplyType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "requirements")
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private SupplyType rewardType;

    @Column(name = "reward_amount")
    private Integer rewardAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "until_date")
    private LocalDate untilDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement_status", nullable = false)
    private RequirementStatus status = RequirementStatus.DRAFT;

    @OneToMany(
        mappedBy = "requirement",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<RequirementCondition> conditions = new ArrayList<>();

    public Requirement() {
    }

    public Requirement(User user, SupplyType rewardType, Integer rewardAmount, LocalDate startDate, LocalDate untilDate) {
        this.user = user;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
        this.startDate = startDate;
        this.untilDate = untilDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SupplyType getRewardType() {
        return rewardType;
    }

    public void setRewardType(SupplyType rewardType) {
        this.rewardType = rewardType;
    }

    public Integer getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(Integer rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(LocalDate untilDate) {
        this.untilDate = untilDate;
    }

    public RequirementStatus getStatus() {
        return status;
    }

    public void setStatus(RequirementStatus status) {
        this.status = status;
    }
}
