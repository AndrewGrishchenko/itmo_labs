package com.andrew.service;

import java.util.List;

import com.andrew.dto.IncidentReport.IncidentReportCreateDTO;
import com.andrew.dto.IncidentReport.IncidentReportResponseDTO;
import com.andrew.dto.fine.FineCreateDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.IncidentReportMapper;
import com.andrew.model.Case;
import com.andrew.model.IncidentReport;
import com.andrew.repository.IncidentReportRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class IncidentReportService {
    @Inject
    IncidentReportRepository incidentReportRepository;

    @Inject
    IncidentReportMapper incidentReportMapper;

    @Inject
    CaseManagementService caseManagementService;

    @Inject
    FineService fineService;

    @Transactional
    public List<IncidentReportResponseDTO> getAll() {
        return incidentReportRepository.getAll().stream().map(incidentReportMapper::toResponse).toList();
    }

    @Transactional
    public IncidentReportResponseDTO createIncidentReport(IncidentReportCreateDTO dto) {
        Case c = caseManagementService.createCase(dto.description());
        IncidentReport incidentReport = incidentReportMapper.toEntity(dto);
        incidentReport.setCase(c);
        
        incidentReportRepository.save(incidentReport);
        return incidentReportMapper.toResponse(incidentReport);
    }

    @Transactional
    public IncidentReport getById(Long id) {
        return incidentReportRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("IncidentReport", id));
    }

    @Transactional
    public void closeIncident(Long id) {
        IncidentReport incidentReport = getById(id);
        caseManagementService.closeCase(incidentReport.getCase());
    }

    @Transactional
    public void fineIncident(Long id, FineCreateDTO dto) {
        IncidentReport incidentReport = getById(id);
        
        fineService.createFine(incidentReport.getCase(), dto.amount());
        
        caseManagementService.closeCase(incidentReport.getCase());
    }

    //TODO: do we need this?
    // @Transactional
    // public IncidentReportResponseDTO updateIncidentReport(Long id, IncidentReportCreateDTO dto) {
    //     incidentReportRepository.findById(id)
    //         .orElseThrow(() -> new NotFoundException("IncidentReport", id));

    //     IncidentReport toUpdate = incidentReportMapper.toEntity(dto);
    //     toUpdate.setId(id);

    //     return incidentReportMapper.toResponse(incidentReportRepository.update(toUpdate));
    // }

    // @Transactional
    // public void deleteIncidentReport(Long id) {
    //     IncidentReport incidentReport = incidentReportRepository.findById(id)
    //         .orElseThrow(() -> new NotFoundException("IncidentReport", id));

    //     incidentReportRepository.delete(incidentReport);
    // }
}
