package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.andrew.dto.requirement.RequirementCreateDTO;
import com.andrew.dto.requirement.RequirementResponseDTO;
import com.andrew.model.Requirement;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RequirementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    Requirement toEntity(RequirementCreateDTO dto);

    @Mapping(target = "userId", source = "user.id")
    RequirementResponseDTO toResponse(Requirement entity);
}
