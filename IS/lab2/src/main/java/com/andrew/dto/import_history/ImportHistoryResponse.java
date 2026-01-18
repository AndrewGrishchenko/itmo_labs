package com.andrew.dto.import_history;

import com.andrew.dto.OwnerResponse;
import com.andrew.model.OperationStatus;

public record ImportHistoryResponse( 
    Long id,
    String creationDate,
    OwnerResponse user,
    OperationStatus operationStatus,
    Integer objectCount
) {}