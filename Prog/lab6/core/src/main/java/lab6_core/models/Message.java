package lab6_core.models;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String header;
    private String[] command;
    private String response;
    private Object obj;

    public Message (String header) {
        this.header = header;
    }

    public Message (String header, String[] command) {
        this.header = header;
        this.command = command;
    }

    public Message (String header, String response) {
        this.header = header;
        this.response = response;
    }

    public Message (String header, Object obj) {
        this.header = header;
        this.obj = obj;
    }

    public String getHeader () {
        return header;
    }

    public Object getObj () {
        return obj;
    }

    public String[] getCommand () {
        return command;
    }

    public String getResponse () {
        return response;
    }
}
