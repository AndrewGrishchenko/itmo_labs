package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.segment.SegmentCreateDTO;
import com.andrew.dto.segment.SegmentResponseDTO;
import com.andrew.model.Route;
import com.andrew.model.Segment;
import com.andrew.model.Zone;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface SegmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", source = "routeId", qualifiedByName = "routeFromId")
    @Mapping(target = "zone", source = "zoneId", qualifiedByName = "zoneFromId")
    Segment toEntity(SegmentCreateDTO dto);

    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "zoneId", source = "zone.id")
    SegmentResponseDTO toResponse(Segment entity);

    @Named("routeFromId")
    default Route routeFromId(Long id) {
        Route route = new Route();
        route.setId(id);
        return route;
    }

    @Named("zoneFromId")
    default Zone zoneFromId(Long id) {
        Zone zone = new Zone();
        zone.setId(id);
        return zone;
    }
}
