package com.andrew.lab1.entity;

import com.andrew.lab1.entity.enums.ComplaintStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "complaints")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rightsholder_id")
    private Long rightsholderId;

    @ManyToOne()
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(columnDefinition = "TEXT", name = "claim_details")
    private String claimDetails;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    @Column(name = "moderator_comment")
    private String moderatorComment;
}
