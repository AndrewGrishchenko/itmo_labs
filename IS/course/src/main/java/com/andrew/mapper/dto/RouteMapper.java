package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.route.RouteCreateDTO;
import com.andrew.dto.route.RouteResponseDTO;
import com.andrew.model.Route;
import com.andrew.model.RouteRequest;
import com.andrew.service.SegmentService;

import jakarta.inject.Inject;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class RouteMapper {
    @Inject
    protected SegmentService segmentService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "routeRequest", source = "routeRequestId", qualifiedByName = "routeRequestFromId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "date", ignore = true)
    public abstract Route toEntity(RouteCreateDTO dto);

    @Mapping(target = "routeRequestId", source = "request.id")
    @Mapping(target = "zoneIds", expression = "java(segmentService.getSegmentsForRoute(entity.getId()))")
    public abstract RouteResponseDTO toResponse(Route entity);

    @Named("routeRequestFromId")
    public RouteRequest routeRequestFromId(Long id) {
        RouteRequest routeRequest = new RouteRequest();
        routeRequest.setId(id);
        return routeRequest;
    }
}
