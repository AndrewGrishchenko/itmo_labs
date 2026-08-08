package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.RequirementCondition.RequirementConditionCreateDTO;
import com.andrew.dto.RequirementCondition.RequirementConditionResponseDTO;
import com.andrew.model.Requirement;
import com.andrew.model.RequirementCondition;
import com.andrew.model.Zone;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RequirementConditionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "zone", source = "zoneId", qualifiedByName = "zoneFromId")
    @Mapping(target = "requirement", source = "requirementId", qualifiedByName = "requirementFromId")
    RequirementCondition toEntity(RequirementConditionCreateDTO dto);

    @Mapping(target = "zoneId", source = "zone.id")
    @Mapping(target = "requirementId", source = "requirement.id")
    RequirementConditionResponseDTO toResponse(RequirementCondition entity);

    @Named("zoneFromId")
    default Zone zoneFromId(Long id) {
        Zone zone = new Zone();
        zone.setId(id);
        return zone;
    }

    @Named("requirementFromId")
    default Requirement requirementFromId(Long id) {
        Requirement requirement = new Requirement();
        requirement.setId(id);
        return requirement;
    }
}
