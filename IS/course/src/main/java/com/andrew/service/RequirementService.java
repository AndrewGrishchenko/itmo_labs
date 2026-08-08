package com.andrew.service;

import java.time.LocalDate;
import java.util.List;

import com.andrew.dto.VisitRequest.VisitRequestCreateDTO;
import com.andrew.dto.VisitRequest.VisitRequestResponseDTO;
import com.andrew.dto.delivery.DeliveryCreateDTO;
import com.andrew.dto.requirement.RequirementCreateDTO;
import com.andrew.dto.requirement.RequirementResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.RequirementMapper;
import com.andrew.model.Requirement;
import com.andrew.model.enums.RequirementStatus;
import com.andrew.repository.RequirementRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class RequirementService {
    @Inject
    RequirementRepository requirementRepository;

    @Inject
    RequirementMapper requirementMapper;

    @Inject
    CurrentUser currentUser;

    @Inject
    VisitRequestService visitRequestService;

    @Inject
    DeliveryService deliveryService;

    @Transactional
    public List<RequirementResponseDTO> getAll() {
        List<Requirement> requirements = switch (currentUser.getUser().getRole()) {
            case OUTGROUP -> requirementRepository.getByUserId(currentUser.getUser().getId());
            case KEEPER, BOSS, ADMIN -> requirementRepository.getAll();
            default -> throw new ForbiddenException();
        };

        return requirements.stream().map(requirementMapper::toResponse).toList();
    }

    @Transactional
    public RequirementResponseDTO createRequirement(RequirementCreateDTO dto) {
        Requirement requirement = requirementMapper.toEntity(dto);
        requirement.setUser(currentUser.getUser());
        
        return createRequirement(requirement);
    }

    @Transactional
    private RequirementResponseDTO createRequirement(Requirement requirement) {
        requirementRepository.save(requirement);
        return requirementMapper.toResponse(requirement);
    }

    @Transactional
    public RequirementResponseDTO editRequirement(Long id, RequirementCreateDTO dto) {
        Requirement requirement = getById(id);
        assertStatus(requirement, RequirementStatus.DRAFT);

        Requirement toUpdate = requirementMapper.toEntity(dto);

        if (!requirement.getUser().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException();

        toUpdate.setId(id);
        toUpdate.setUser(currentUser.getUser());

        return requirementMapper.toResponse(requirementRepository.update(toUpdate));
    }

    @Transactional
    public void deleteRequirement(Long id) {
        Requirement requirement = getById(id);
        
        if (!(requirement.getStatus().equals(RequirementStatus.DRAFT) ||
            requirement.getStatus().equals(RequirementStatus.ACTIVE)))
            throw new ForbiddenException();

        requirementRepository.delete(requirement);
    }

    @Transactional
    public Requirement getById(Long id) {
        return requirementRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Requirement", id));
    }

    @Transactional
    public void assertStatus(Long id, RequirementStatus status) {
        if (!getById(id).getStatus().equals(status))
            throw new ForbiddenException();
    }

    @Transactional
    public void assertStatus(Requirement requirement, RequirementStatus status) {
        if (!requirement.getStatus().equals(status))
            throw new ForbiddenException();
    }

    @Transactional
    public void submitRequirement(Long id) {
        Requirement requirement = getById(id);
        assertStatus(requirement, RequirementStatus.DRAFT);
        requirement.setStatus(RequirementStatus.ACTIVE);
        requirementRepository.update(requirement);
    }

    @Transactional
    public void calculateFulfillment() {
        List<Requirement> active = requirementRepository.getActive();

        for (Requirement req : active) {
            if (LocalDate.now().isAfter(req.getUntilDate())) {
                finalizeRequirement(req);
            }
        }
    }

    @Transactional
    private void finalizeRequirement(Requirement requirement) {
        boolean isFulfilled = requirementRepository.isFulfilled(requirement.getId());

        if (isFulfilled) {
            requirement.setStatus(RequirementStatus.COMPLETED);
            requirementRepository.update(requirement);
            createRewardVisit(requirement);
        } else {
            requirement.setStatus(RequirementStatus.FAILED);
            requirementRepository.update(requirement);
        }
    }

    @Transactional
    private void createRewardVisit(Requirement requirement) {
        VisitRequestResponseDTO visitRequest = visitRequestService.createVisitRequest(
            new VisitRequestCreateDTO(
                currentUser.getUser().getId(),
                LocalDate.now()
            )
        );

        deliveryService.createDelivery(
            new DeliveryCreateDTO(
                requirement.getRewardType(),
                visitRequest.id(),
                requirement.getRewardAmount()
            )
        );

        visitRequestService.submitVisitRequest(visitRequest.id());
    }
}
