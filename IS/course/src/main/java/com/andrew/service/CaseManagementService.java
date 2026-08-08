package com.andrew.service;

import com.andrew.mapper.dto.CaseMapper;
import com.andrew.model.Case;
import com.andrew.model.enums.CaseStatus;
import com.andrew.repository.CaseRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class CaseManagementService {
    @Inject
    CaseRepository caseRepository;

    @Inject
    CaseMapper caseMapper;

    @Transactional
    public Case createCase(String description) {
        Case c = new Case(description);
        caseRepository.save(c);
        return c;
    }

    @Transactional
    public void closeCase(Case c) {
        if (!c.getStatus().equals(CaseStatus.OPEN))
            throw new ForbiddenException();

        c.setStatus(CaseStatus.CLOSED);
        caseRepository.update(c);
    }
}
