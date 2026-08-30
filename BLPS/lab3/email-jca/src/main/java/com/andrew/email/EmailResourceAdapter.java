package com.andrew.email;

import javax.transaction.xa.XAResource;

import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.ResourceAdapterInternalException;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

public class EmailResourceAdapter implements ResourceAdapter {
    private BootstrapContext bootstrapContext;

    public EmailResourceAdapter() {
    }

    @Override
    public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
        this.bootstrapContext = bootstrapContext;

        System.out.println("Email JCA Resource Adapter started");
    }

    @Override
    public void stop() {
        System.out.println("Email JCA Resource Adapter stopped");

        bootstrapContext = null;
    }

    @Override
    public XAResource[] getXAResources(
            ActivationSpec[] specs)
            throws ResourceAdapterInternalException {

        return new XAResource[0];
    }

    @Override
    public void endpointActivation(
            MessageEndpointFactory endpointFactory,
            ActivationSpec activationSpec)
            throws ResourceAdapterInternalException {
        
        throw new ResourceAdapterInternalException(
            "Inbound endpoints are not supported"
        );
    }

    @Override
    public void endpointDeactivation(
            MessageEndpointFactory endpointFactory,
            ActivationSpec activationSpec) {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof EmailResourceAdapter;
    }

    @Override
    public int hashCode() {
        return EmailResourceAdapter.class.hashCode();
    }
}
