package lab7_server.models;

import lab7_core.models.Message;
import lab7_server.managers.AuthManager;

public class ClientData {
    private AuthManager authManager;
    private Message message;

    public ClientData () {
        this.authManager = new AuthManager();
    }

    public AuthManager getAuthManager () {
        return authManager;
    }

    public void setAuthManager (AuthManager authManager) {
        this.authManager = authManager;
    }

    public Message getMessage () {
        return message;
    }

    public void setMessage (Message message) {
        this.message = message;
    }
}
