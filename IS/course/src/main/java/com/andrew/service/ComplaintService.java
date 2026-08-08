package com.andrew.service;

import java.util.List;

import com.andrew.dto.complaint.ComplaintCreateDTO;
import com.andrew.dto.complaint.ComplaintResponseDTO;
import com.andrew.dto.fine.FineCreateDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.ComplaintMapper;
import com.andrew.model.Case;
import com.andrew.model.Complaint;
import com.andrew.repository.ComplaintRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class ComplaintService {
    @Inject
    ComplaintRepository complaintRepository;

    @Inject
    ComplaintMapper complaintMapper;

    @Inject
    RouteManagementService routeManagementService;

    @Inject
    CaseManagementService caseManagementService;

    @Inject
    FineService fineService;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public List<ComplaintResponseDTO> getAll() {
        List<Complaint> complaints = switch (currentUser.getUser().getRole()) {
            case CAPTAIN -> complaintRepository.getByCaptainId(currentUser.getUser().getId());
            case KEEPER, BOSS, ADMIN -> complaintRepository.getAll();
            default -> throw new ForbiddenException();
        };

        return complaints.stream().map(complaintMapper::toResponse).toList();
    }

    //TODO: maybe check uniqueness in logic, here, instead of postgres exception
    @Transactional
    public ComplaintResponseDTO createComplaint(ComplaintCreateDTO dto) {
        routeManagementService.assertCanBeRequestComplained(dto.routeRequestId());
        
        Case c = caseManagementService.createCase(dto.description());
        Complaint complaint = complaintMapper.toEntity(dto);
        complaint.setCase(c);
        
        complaintRepository.save(complaint);
        return complaintMapper.toResponse(complaint);
    }

    @Transactional
    public Complaint getBydId(Long id) {
        return complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));
    }

    @Transactional
    public void closeComplaint(Long id) {
        Complaint complaint = getBydId(id);
        caseManagementService.closeCase(complaint.getCase());
    }

    @Transactional
    public void fineComplaint(Long id, FineCreateDTO dto) {
        Complaint complaint = getBydId(id);

        fineService.createFine(complaint.getCase(), dto.amount());

        caseManagementService.closeCase(complaint.getCase());
    }

    //TODO: do we need this fr?
    // @Transactional
    // public ComplaintResponseDTO updateComplaint(Long id, ComplaintCreateDTO dto) {
    //     complaintRepository.findById(id)
    //         .orElseThrow(() -> new NotFoundException("Complaint", id));

    //     Complaint toUpdate = complaintMapper.toEntity(dto);
    //     toUpdate.setId(id);

    //     return complaintMapper.toResponse(complaintRepository.update(toUpdate));
    // }

    // @Transactional
    // public void deleteComplaint(Long id) {
    //     Complaint complaint = complaintRepository.findById(id)
    //         .orElseThrow(() -> new NotFoundException("Complaint", id));

    //     complaintRepository.delete(complaint);
    // }
}
