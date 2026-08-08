package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.andrew.dto.cases.CaseCreateDTO;
import com.andrew.dto.cases.CaseResponseDTO;
import com.andrew.model.Case;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface CaseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Case toEntity(CaseCreateDTO dto);

    CaseResponseDTO toResponse(Case entity);
}
