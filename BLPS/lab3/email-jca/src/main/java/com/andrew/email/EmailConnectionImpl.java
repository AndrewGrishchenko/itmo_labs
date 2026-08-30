package com.andrew.email;

import jakarta.resource.ResourceException;

public class EmailConnectionImpl implements EmailConnection {
    private final EmailManagedConnection managedConnection;

    public EmailConnectionImpl(EmailManagedConnection managedConnection) {
        this.managedConnection = managedConnection;
    }

    @Override
    public void send(Email email) throws ResourceException {
        managedConnection.send(email);
    }

    @Override
    public void close() throws ResourceException {
        managedConnection.closeHandle(this);
    }
}
