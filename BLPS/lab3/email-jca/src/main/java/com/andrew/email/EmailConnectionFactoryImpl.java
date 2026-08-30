package com.andrew.email;

import java.io.Serializable;

import javax.naming.Reference;

import jakarta.resource.Referenceable;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;

public class EmailConnectionFactoryImpl implements EmailConnectionFactory, Serializable, Referenceable {
    private static final long serialVersionUID = 1L;
    
    private final EmailManagedConnectionFactory mcf;
    private final ConnectionManager connectionManager;
    private Reference reference;

    public EmailConnectionFactoryImpl(
            EmailManagedConnectionFactory mcf,
            ConnectionManager connectionManager) {
        
        this.mcf = mcf;
        this.connectionManager = connectionManager;
    }

    @Override
    public EmailConnection getConnection() throws ResourceException {
        ConnectionRequestInfo cri = 
            new EmailConnectionRequestInfo(
                mcf.getHost(),
                mcf.getPort()
            );
        
        return (EmailConnection) connectionManager.allocateConnection(mcf, cri);
    }

    @Override 
    public Reference getReference() {
        return reference;
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }
}
