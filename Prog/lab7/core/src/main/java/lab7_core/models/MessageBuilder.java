package lab7_core.models;

public class MessageBuilder {
    private String[] command;
    private String response;
    private Object obj;

    public MessageBuilder command (String[] command) {
        this.command = command;
        return this;
    }

    public MessageBuilder response (String response) {
        this.response = response;
        return this;
    }

    public MessageBuilder obj (Object obj) {
        this.obj = obj;
        return this;
    }

    public Message build () {
        return new Message(command, response, obj);
    }
}
