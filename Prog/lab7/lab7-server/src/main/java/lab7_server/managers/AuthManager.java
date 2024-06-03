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

    private Integer userId = null;

    public AuthManager () {
    }

    public void register (String username, String password) {
        logger.info("Registering new user " + username);

        String hash = generateHash(password);
        User user = new User(username, hash);

        int id = DBManager.executeInsert("users", user);
        if (id != -1) {
            System.out.println("User registered!");
        } else {
            System.out.println("User already exists!");
        }
    }

    public boolean login (String username, String password) {
        logger.info("Logging in user " + username);

        String hash = generateHash(password);
        
        ArrayList<Object> users = DBManager.executeSelect("users");
        
        if (users.size() == 0) {
            System.out.println("No such user found!");
            return false;
        }

        User user = (User) users.get(0);
        if (!hash.equals(user.getHash())) {
            System.out.println("Password is not correct!");
            return false;
        }

        System.out.println("Logged in successfully!");
        return true;
    }

    private String generateHash (String password) {
        try {
            byte[] passwordBytes = password.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(passwordBytes);
            return new String(digest, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
