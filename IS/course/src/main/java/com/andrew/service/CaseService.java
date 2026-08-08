package com.andrew.service;

import com.andrew.dto.cases.CaseCreateDTO;
import com.andrew.dto.cases.CaseResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.CaseMapper;
import com.andrew.model.Case;
import com.andrew.repository.CaseRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CaseService {
    @Inject
    CaseRepository caseRepository;

    @Inject
    CaseMapper caseMapper;

    @Transactional
    public CaseResponseDTO createCase(CaseCreateDTO dto) {
        return createCase(caseMapper.toEntity(dto));
    }

    @Transactional
    private CaseResponseDTO createCase(Case caseValue) {
        caseRepository.save(caseValue);
        return caseMapper.toResponse(caseValue);
    }

    @Transactional
    public CaseResponseDTO updateCase(Long id, CaseCreateDTO dto) {
        caseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Case", id));

        Case toUpdate = caseMapper.toEntity(dto);
        toUpdate.setId(id);

        return caseMapper.toResponse(caseRepository.update(toUpdate));
    }

    @Transactional
    public void deleteCase(Long id) {
        Case caseValue = caseRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Case", id));

        caseRepository.delete(caseValue);
    }
}
