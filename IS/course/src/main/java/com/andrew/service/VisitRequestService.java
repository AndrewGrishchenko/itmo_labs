package com.andrew.service;

import java.util.List;

import com.andrew.dto.VisitRequest.VisitRequestCreateDTO;
import com.andrew.dto.VisitRequest.VisitRequestResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.VisitRequestMapper;
import com.andrew.model.VisitRequest;
import com.andrew.model.enums.VisitRequestStatus;
import com.andrew.repository.VisitRequestRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class VisitRequestService {
    @Inject
    VisitRequestRepository visitRequestRepository;

    @Inject
    VisitRequestMapper visitRequestMapper;

    @Inject
    SupplyService supplyService;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public VisitRequest getById(Long id) {
        return visitRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("VisitRequest", id));
    }

    @Transactional
    public List<VisitRequestResponseDTO> getAll() {
        List<VisitRequest> visitRequests = switch (currentUser.getUser().getRole()) {
            case CAPTAIN, OUTGROUP -> visitRequestRepository.getByUserId(currentUser.getUser().getId());
            case KEEPER, BOSS, ADMIN -> visitRequestRepository.getAll();
            default -> throw new ForbiddenException();
        };

        return visitRequests.stream().map(visitRequestMapper::toResponse).toList();
    }

    @Transactional
    public VisitRequestResponseDTO createVisitRequest(VisitRequestCreateDTO dto) {
        return createVisitRequest(visitRequestMapper.toEntity(dto));
    }

    @Transactional
    private VisitRequestResponseDTO createVisitRequest(VisitRequest visitRequest) {
        visitRequestRepository.save(visitRequest);
        return visitRequestMapper.toResponse(visitRequest);
    }

    @Transactional
    public void submitVisitRequest(Long id) {
        VisitRequest visitRequest = getById(id);
        
        if (!visitRequest.getStatus().equals(VisitRequestStatus.DRAFT))
            throw new ForbiddenException();
        
        visitRequest.setStatus(VisitRequestStatus.SUBMITTED);
        visitRequestRepository.update(visitRequest);
    }

    @Transactional
    public void approveVisitRequest(Long id) {
        VisitRequest visitRequest = getById(id);

        if (!visitRequest.getStatus().equals(VisitRequestStatus.SUBMITTED))
            throw new ForbiddenException();

        visitRequest.setStatus(VisitRequestStatus.APPROVED);
        visitRequestRepository.update(visitRequest);
    }

    @Transactional
    public void rejectVisitRequest(Long id) {
        VisitRequest visitRequest = getById(id);

        if (!visitRequest.getStatus().equals(VisitRequestStatus.SUBMITTED))
            throw new ForbiddenException();

        visitRequest.setStatus(VisitRequestStatus.REJECTED);
        visitRequestRepository.update(visitRequest);
    }

    @Transactional
    public void completeVisitRequest(Long id) {
        VisitRequest visitRequest = getById(id);

        if (!visitRequest.getStatus().equals(VisitRequestStatus.APPROVED))
            throw new ForbiddenException();

        visitRequest.setStatus(VisitRequestStatus.COMPLETED);
        visitRequestRepository.update(visitRequest);
    }

    @Transactional
    public VisitRequestResponseDTO updateVisitRequest(Long id, VisitRequestCreateDTO dto) {
        visitRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("VisitRequest", id));
        
        VisitRequest toUpdate = visitRequestMapper.toEntity(dto);
        toUpdate.setId(id);
        
        return visitRequestMapper.toResponse(visitRequestRepository.update(toUpdate));
    }

    @Transactional
    public void deleteVisitRequest(Long id) {
        VisitRequest visitRequest = visitRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("VisitRequest", id));

        if (!visitRequest.getStatus().equals(VisitRequestStatus.DRAFT))
            throw new ForbiddenException();
        
        visitRequestRepository.delete(visitRequest);
    }
}
