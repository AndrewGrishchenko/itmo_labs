package com.andrew.dto;

import java.util.List;

public record PageResponse<T> (
    List<T> content,
    long totalElements
) {}
