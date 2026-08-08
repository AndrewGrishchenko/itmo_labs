package com.andrew.service;

import java.util.List;

import com.andrew.dto.RequirementCondition.RequirementConditionCreateDTO;
import com.andrew.dto.RequirementCondition.RequirementConditionResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.RequirementConditionMapper;
import com.andrew.model.Requirement;
import com.andrew.model.RequirementCondition;
import com.andrew.model.Role;
import com.andrew.model.enums.RequirementStatus;
import com.andrew.repository.RequirementConditionRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class RequirementConditionService {
    @Inject
    RequirementConditionRepository requirementConditionRepository;

    @Inject
    RequirementConditionMapper requirementConditionMapper;

    @Inject
    RequirementService requirementService;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public List<RequirementConditionResponseDTO> getByRequirement(Long id) {
        Requirement requirement = requirementService.getById(id);
        
        if (currentUser.getUser().getRole().equals(Role.OUTGROUP) &&
            !requirement.getUser().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException();

        return requirementConditionRepository.getByRequirementId(id).stream()
            .map(requirementConditionMapper::toResponse)
            .toList();
    }

    @Transactional
    public RequirementConditionResponseDTO createRequirementCondition(RequirementConditionCreateDTO dto) {
        requirementService.assertStatus(dto.requirementId(), RequirementStatus.DRAFT);
        return createRequirementCondition(requirementConditionMapper.toEntity(dto));
    }

    @Transactional
    private RequirementConditionResponseDTO createRequirementCondition(RequirementCondition requirementCondition) {
        requirementConditionRepository.save(requirementCondition);
        return requirementConditionMapper.toResponse(requirementCondition);
    }

    @Transactional
    public RequirementConditionResponseDTO updateRequirementCondition(Long id, RequirementConditionCreateDTO dto) {
        RequirementCondition existing = getById(id);

        requirementService.assertStatus(existing.getRequirement(), RequirementStatus.DRAFT);

        RequirementCondition toUpdate = requirementConditionMapper.toEntity(dto);
        toUpdate.setId(id);

        return requirementConditionMapper.toResponse(requirementConditionRepository.update(toUpdate));
    }

    @Transactional
    public void deleteRequirementCondition(Long id) {
        RequirementCondition requirementCondition = getById(id);

        requirementService.assertStatus(requirementCondition.getRequirement(), RequirementStatus.DRAFT);

        requirementConditionRepository.delete(requirementCondition);
    }

    @Transactional
    public RequirementCondition getById(Long id) {
        return requirementConditionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("RequirementCondition", id));
    }
}
