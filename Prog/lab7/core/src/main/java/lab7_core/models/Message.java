package lab7_core.models;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String[] command;
    private final String response;
    private final Object obj;

    public Message (String[] command, String response, Object obj) {
        this.command = command;
        this.response = response;
        this.obj = obj;
    }

    public String[] getCommand() {
        return command;
    }

    public String getResponse () {
        return response;
    }

    public Object getObject() {
        return obj;
    }
}
