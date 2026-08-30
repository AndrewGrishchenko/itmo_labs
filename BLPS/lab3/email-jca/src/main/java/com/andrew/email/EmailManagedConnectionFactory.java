package com.andrew.email;

import java.io.PrintWriter;
import java.util.Set;

import javax.security.auth.Subject;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionDefinition;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.ResourceAdapterAssociation;

@ConnectionDefinition(
        connectionFactory = EmailConnectionFactory.class,
        connectionFactoryImpl = EmailConnectionFactoryImpl.class,
        connection = EmailConnection.class,
        connectionImpl = EmailConnectionImpl.class
)
public class EmailManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {
    private String host = "localhost";
    private int port = 1025;

    private ResourceAdapter resourceAdapter;
    private PrintWriter logWriter;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public EmailManagedConnectionFactory() {
    }

    @Override
    public Object createConnectionFactory(ConnectionManager connectionManager) throws ResourceException {
        return new EmailConnectionFactoryImpl(this, connectionManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        throw new ResourceException("ConnectionManager is required");
    }

    @Override
    public ManagedConnection createManagedConnection(
            Subject subject,
            ConnectionRequestInfo requestInfo)
            throws ResourceException {

        EmailConnectionRequestInfo info = (EmailConnectionRequestInfo) requestInfo;

        return new EmailManagedConnection(host, port);
    }

    @Override
    public ManagedConnection matchManagedConnections(
            Set connectionSet,
            Subject subject,
            ConnectionRequestInfo requestInfo)
            throws ResourceException {

        if (connectionSet == null || connectionSet.isEmpty()) {
            return null;
        }

        EmailConnectionRequestInfo info = (EmailConnectionRequestInfo) requestInfo;

        for (Object object : connectionSet) {
            EmailManagedConnection connection = (EmailManagedConnection) object;
            return connection;
        }

        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
        this.resourceAdapter = resourceAdapter;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    @Override
    public int hashCode() {
        return host.hashCode() * 31 + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EmailManagedConnectionFactory other)) {
            return false;
        }

        return host.equals(other.host)
                && port == other.port;
    }
}
