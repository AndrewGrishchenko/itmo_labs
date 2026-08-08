package com.andrew.model;

import java.util.List;

import com.andrew.model.enums.RouteRequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_requests")
public class RouteRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ship_id", nullable = false)
    private Ship ship;

    @ManyToOne
    @JoinColumn(name = "source_zone_id", nullable = false)
    private Zone sourceZone;

    @ManyToOne
    @JoinColumn(name = "target_zone_id", nullable = false)
    private Zone targetZone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Goal goal;

    @ManyToMany
    @JoinTable(
        name = "route_documents",
        joinColumns = @JoinColumn(name = "route_request_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> documents;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RouteRequestStatus status = RouteRequestStatus.DRAFT;

    public RouteRequest() {
    }

    public RouteRequest(Ship ship, Zone sourceZone, Zone targetZone, Goal goal, List<Document> documents) {
        this.ship = ship;
        this.sourceZone = sourceZone;
        this.targetZone = targetZone;
        this.goal = goal;
        this.documents = documents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Zone getSourceZone() {
        return sourceZone;
    }

    public void setSourceZone(Zone sourceZone) {
        this.sourceZone = sourceZone;
    }

    public Zone getTargetZone() {
        return targetZone;
    }

    public void setTargetZone(Zone targetZone) {
        this.targetZone = targetZone;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public RouteRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RouteRequestStatus status) {
        this.status = status;
    }
}
