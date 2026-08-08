package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.andrew.dto.supply.SupplyResponseDTO;
import com.andrew.model.Supply;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface SupplyMapper {
    SupplyResponseDTO toResponse(Supply entity);
}
