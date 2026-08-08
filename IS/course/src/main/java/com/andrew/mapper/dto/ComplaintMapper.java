package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.complaint.ComplaintCreateDTO;
import com.andrew.dto.complaint.ComplaintResponseDTO;
import com.andrew.model.Complaint;
import com.andrew.model.RouteRequest;
import com.andrew.service.FineService;

import jakarta.inject.Inject;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class ComplaintMapper {
    @Inject
    protected FineService fineService;
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "case", ignore = true)
    @Mapping(target = "routeRequest", source = "routeRequestId", qualifiedByName = "routeRequestFromId")
    public abstract Complaint toEntity(ComplaintCreateDTO dto);

    @Mapping(target = "caseId", source = "case.id")
    @Mapping(target = "routeRequestId", source = "routeRequest.id")
    @Mapping(target = "description", source = "case.description")
    @Mapping(target = "status", source = "case.status")
    @Mapping(target = "fineAmount", expression = "java(fineService.findForCase(entity.getCase().getId()))")
    public abstract ComplaintResponseDTO toResponse(Complaint entity);

    @Named("routeRequestFromId")
    public RouteRequest routeRequestFromId(Long id) {
        RouteRequest routeRequest = new RouteRequest();
        routeRequest.setId(id);
        return routeRequest;
    }
}
