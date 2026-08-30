package com.andrew.email;

import jakarta.resource.ResourceException;

public interface EmailConnection {
    void send(Email email) throws ResourceException;

    void close() throws ResourceException;
}