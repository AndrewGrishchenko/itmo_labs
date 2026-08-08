package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.IncidentReport.IncidentReportCreateDTO;
import com.andrew.dto.IncidentReport.IncidentReportResponseDTO;
import com.andrew.model.Case;
import com.andrew.model.IncidentReport;
import com.andrew.model.Supply;
import com.andrew.service.FineService;

import jakarta.inject.Inject;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class IncidentReportMapper {
    @Inject
    protected FineService fineService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "case", ignore = true)
    public abstract IncidentReport toEntity(IncidentReportCreateDTO dto);

    @Mapping(target = "caseId", source = "case.id")
    @Mapping(target = "description", source = "case.description")
    @Mapping(target = "status", source = "case.status")
    @Mapping(target = "fineAmount", expression = "java(fineService.findForCase(entity.getCase().getId()))")
    public abstract IncidentReportResponseDTO toResponse(IncidentReport entity);

    @Named("caseFromId")
    public Case caseFromId(Long id) {
        Case caseValue = new Case();
        caseValue.setId(id);
        return caseValue;
    }

    @Named("supplyFromId")
    public Supply supplyFromId(Long id) {
        Supply supply = new Supply();
        supply.setId(id);
        return supply;
    }
}
