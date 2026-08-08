package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.andrew.dto.zone.ZoneCreateDTO;
import com.andrew.dto.zone.ZoneResponseDTO;
import com.andrew.model.Zone;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface ZoneMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "connections", ignore = true)
    Zone toEntity(ZoneCreateDTO dto);

    ZoneResponseDTO toResponse(Zone entity);
}
