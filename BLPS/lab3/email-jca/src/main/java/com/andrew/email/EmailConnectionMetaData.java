package com.andrew.email;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ManagedConnectionMetaData;

public class EmailConnectionMetaData implements ManagedConnectionMetaData {
    @Override
    public String getEISProductName() throws ResourceException {
        return "Mailpit";
    }

    @Override
    public String getEISProductVersion() throws ResourceException {
        return "1.0";
    }

    @Override
    public int getMaxConnections() throws ResourceException {
        return 0;
    }

    @Override
    public String getUserName() throws ResourceException {
        return "";
    }
}
