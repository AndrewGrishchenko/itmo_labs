package lab7_core.models;

public class User {
    private int id;
    private final String username;
    private final String hash;

    public User (int id, String username, String hash) {
        this.id = id;
        this.username = username;
        this.hash = hash;
    }

    public User (String username, String hash) {
        this.username = username;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public String getUsername () {
        return username;
    }

    public String getHash () {
        return hash;
    }

    @Override
    public String toString () {
        return "User{\n"
            + "  id='" + String.valueOf(id) + "'\n"
            + "  username='" + username + "'\n"
            + "  hash='" + hash + "'}";
    }
}
