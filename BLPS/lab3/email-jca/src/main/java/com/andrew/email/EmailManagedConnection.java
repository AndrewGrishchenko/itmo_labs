package com.andrew.email;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEvent;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;

public class EmailManagedConnection implements ManagedConnection {
    private final String host;
    private final int port;

    private final List<ConnectionEventListener> listeners = new ArrayList<>();

    private PrintWriter logWriter;

    public EmailManagedConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public EmailConnection getConnection() {
        return new EmailConnectionImpl(this);
    }

    @Override
    public Object getConnection(
            Subject subject,
            ConnectionRequestInfo connectionRequestInfo)
            throws ResourceException {
        
        return new EmailConnectionImpl(this);       
    }

    public void send(Email email) throws ResourceException {
        try {
            Properties properties = new Properties();

            properties.put(
                "mail.smtp.host",
                host
            );

            properties.put(
                "mail.smtp.port",
                String.valueOf(port)
            );

            properties.put(
                "mail.smtp.auth",
                "false"
            );

            properties.put(
                "mail.smtp.starttls.enable",
                "false"
            );

            Session session = Session.getInstance(properties);

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(email.getFrom()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
            message.setSubject(email.getSubject(), "UTF-8");
            message.setText(email.getBody(), "UTF-8");

            Transport.send(message);
        } catch (MessagingException e) {
            fireConnectionError();

            throw new ResourceException("failed to send email", e);
        }
    }

    private void fireConnectionError() {
        ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_ERROR_OCCURRED);
        
        for (ConnectionEventListener listener : listeners) {
            listener.connectionErrorOccurred(event);
        }
    }

    public void closeHandle(EmailConnectionImpl connection) {
        ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        event.setConnectionHandle(connection);

        for (ConnectionEventListener listener : listeners) {
            listener.connectionClosed(event);
        }
    }

    @Override
    public void destroy() throws ResourceException {
    }

    @Override
    public void cleanup() throws ResourceException {
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new ResourceException("XA not supported");
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new ResourceException("Transactions not supported");
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new EmailConnectionMetaData();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }
}
