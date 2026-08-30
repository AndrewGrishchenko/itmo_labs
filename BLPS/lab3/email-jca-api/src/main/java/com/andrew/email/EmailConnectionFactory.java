package com.andrew.email;

import jakarta.resource.ResourceException;

public interface EmailConnectionFactory {
    EmailConnection getConnection()
            throws ResourceException;
}