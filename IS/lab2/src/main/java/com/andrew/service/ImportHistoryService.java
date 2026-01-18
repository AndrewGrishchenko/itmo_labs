package com.andrew.service;

import com.andrew.dto.PageResponse;
import com.andrew.dto.import_history.ImportHistoryFilter;
import com.andrew.dto.import_history.ImportHistoryResponse;
import com.andrew.model.ImportHistory;
import com.andrew.model.OperationStatus;
import com.andrew.model.User;
import com.andrew.repository.ImportHistoryRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.ResponseMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ImportHistoryService {
    @Inject
    ImportHistoryRepository importHistoryRepository;

    @Inject
    CurrentUser currentUser;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createImportHistory(User user, OperationStatus operationStatus, Integer objectCount) {
        ImportHistory importHistory = new ImportHistory(user, operationStatus, objectCount);
        importHistoryRepository.save(importHistory);
    }

    @Transactional
    public PageResponse<ImportHistoryResponse> getAllImportHistory(int page, int size, String sort, String order, ImportHistoryFilter filter) {
        if (!currentUser.isAdmin())
            filter = new ImportHistoryFilter(filter.id(), currentUser.getUser().getId(), filter.operationStatus(), filter.objectCount());

        PageResponse<ImportHistory> pagedResponse = importHistoryRepository.findAllPaginatedAndSorted(page, size, sort, order, filter);

        return new PageResponse<ImportHistoryResponse>(
            pagedResponse.content().stream()
                .map(ResponseMapper::toResponse)
                .toList(),
            pagedResponse.totalElements()
        );
    }

    @Transactional
    public ImportHistoryResponse getById(int id) {
        ImportHistory importHistory = importHistoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Import History with id " + id + " not found"));
        
        if (importHistory.getUser() != currentUser.getUser() && !currentUser.isAdmin())
            throw new ForbiddenException();

        return ResponseMapper.toResponse(importHistory);
    }
}
