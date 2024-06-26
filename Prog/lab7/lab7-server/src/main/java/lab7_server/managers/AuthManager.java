package lab7_server.managers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;

import lab7_core.models.User;
import lab7_server.Main;

public class AuthManager {
    private Logger logger = Main.logger;

    private Integer userId;

    public AuthManager () {
    }

    public String register (String username, String password) {
        logger.info("Registering new user " + username);

        String hash = generateHash(password);
        User user = new User(username, hash);

        int id = DBManager.executeInsert("users", user);
        if (id != -1) {
            userId = user.getId();
            return "User registered!";
        } else {
            return "User already exists!";
        }
    }

    public String login (String username, String password) {
        logger.info("Logging in user " + username);

        String hash = generateHash(password);
        
        ArrayList<Object> users = DBManager.executeSelect("users", "username='" + username + "';");

        if (users.size() == 0) {
            return "No such user found!";
        }

        User user = (User) users.get(0);
        if (!hash.equals(user.getHash())) {
            return "Password is not correct!";
        }

        userId = user.getId();
        return "Logged in successfully!";
    }

    public void logout () {
        userId = null;
    }

    private String generateHash (String password) {
        if (password == "") return password;
        
        try {
            byte[] passwordBytes = password.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(passwordBytes);
            return new String(digest, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public boolean isLoggedIn () {
        return userId == null ? false : true;
    }

    public Integer getUserId () {
        return userId;
    }
}
