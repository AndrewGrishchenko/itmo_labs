package com.andrew.email;

import java.util.Objects;

import jakarta.resource.spi.ConnectionRequestInfo;

public class EmailConnectionRequestInfo implements ConnectionRequestInfo {
    private final String host;
    private final int port;

    public EmailConnectionRequestInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EmailConnectionRequestInfo other)) {
            return false;
        }

        return port == other.port
                && Objects.equals(host, other.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
