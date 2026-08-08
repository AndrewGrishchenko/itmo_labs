package com.andrew.mapper.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.RouteRequest.RouteRequestCreateDTO;
import com.andrew.dto.RouteRequest.RouteRequestResponseDTO;
import com.andrew.model.Document;
import com.andrew.model.RouteRequest;
import com.andrew.model.Ship;
import com.andrew.model.Zone;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RouteRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ship", source = "shipId", qualifiedByName = "shipFromId")
    @Mapping(target = "sourceZone", source = "sourceZoneId", qualifiedByName = "zoneFromId")
    @Mapping(target = "targetZone", source = "targetZoneId", qualifiedByName = "zoneFromId")
    @Mapping(target = "documents", source = "documentsIds", qualifiedByName = "documentsFromIds")
    @Mapping(target = "status", ignore = true)
    RouteRequest toEntity(RouteRequestCreateDTO dto);

    @Mapping(target = "shipId", source = "ship.id")
    @Mapping(target = "sourceZoneId", source = "sourceZone.id")
    @Mapping(target = "targetZoneId", source = "targetZone.id")
    @Mapping(target = "documentsIds", source = "documents", qualifiedByName = "documentsToIds")
    @Mapping(target = "shipType", source = "ship.shipType")
    RouteRequestResponseDTO toResponse(RouteRequest entity);

    @Named("shipFromId")
    default Ship shipFromId(Long id) {
        Ship ship = new Ship();
        ship.setId(id);
        return ship;
    }

    @Named("zoneFromId")
    default Zone zoneFromId(Long id) {
        Zone zone = new Zone();
        zone.setId(id);
        return zone;
    }

    @Named("documentsFromIds")
    default List<Document> documentsFromIds(List<Long> ids) {
        return ids.stream().map(id -> {
            Document d = new Document();
            d.setId(id);
            return d;
        }).toList();
    }

    @Named("documentsToIds")
    default List<Long> documentsToIds(List<Document> documents) {
        return documents.stream().map(Document::getId).toList();
    }
}
