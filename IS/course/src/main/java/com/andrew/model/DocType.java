package com.andrew.model;

import java.util.Optional;

public enum DocType {
    CAPTAIN_ID,
    SHIP_ID,
    CARGO_ID,
    PASS_ID;

    public static Optional<DocType> from(String value) {
        if (value == null) return Optional.empty();

        try {
            return Optional.of(DocType.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
