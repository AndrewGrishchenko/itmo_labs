package com.andrew.dto.import_history;

import com.andrew.model.OperationStatus;

public record ImportHistoryFilter(
    Long id,
    Long userId,
    OperationStatus operationStatus,
    Integer objectCount
) {}
