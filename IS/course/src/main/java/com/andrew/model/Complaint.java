package com.andrew.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseValue;

    @ManyToOne
    @JoinColumn(name = "route_request_id", nullable = false, unique = true)
    private RouteRequest routeRequest;

    public Complaint() {
    }

    public Complaint(Case caseValue, RouteRequest routeRequest) {
        this.caseValue = caseValue;
        this.routeRequest = routeRequest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Case getCase() {
        return caseValue;
    }

    public void setCase(Case caseValue) {
        this.caseValue = caseValue;
    }

    public RouteRequest getRouteRequest() {
        return routeRequest;
    }

    public void setRouteRequest(RouteRequest routeRequest) {
        this.routeRequest = routeRequest;
    }
}
