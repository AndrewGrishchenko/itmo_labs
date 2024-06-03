package lab7_server.commands;

import java.util.Objects;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import lab7_server.Main;
import lab7_server.interfaces.Validatable;
import lab7_server.managers.AuthManager;

public abstract class Command extends RecursiveTask<String> implements Validatable {
    private final String name;
    private final String description;
    private final String usage;
    private final String requiredObject;
    private AuthManager authManager;
    
    private String[] args;
    private Object obj;

    private ReentrantLock lock;

    public Command (String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.requiredObject = null;
    }

    public Command (String name, String description, String usage, String requiredObject) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.requiredObject = requiredObject;
    }

    public String getName () {
        return name;
    }

    public String getDescription () {
        return description;
    }

    public String getUsage () {
        return "Использование: " + usage;
    }

    public String getRequiredObject () {
        return requiredObject;
    }

    public String[] getArgs () {
        return args;
    }

    public void setArgs (String[] args) {
        this.args = args;
    }

    public Object getObj () {
        return obj;
    }

    public void setObj (Object obj) {
        this.obj = obj;
    }

    public void setLock (ReentrantLock lock) {
        this.lock = lock;
    }

    public AuthManager getAuthManager () {
        return authManager;
    }

    public void setAuthManager (AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command other = (Command) obj;
        return Objects.equals(name, other.name)
            && Objects.equals(description, other.description) 
            && Objects.equals(usage, other.usage);
    }

    @Override
    public String toString () {
        return usage + ": " + description;
    }

    @Override
    public String compute () {
        System.out.println(authManager.isLoggedIn());
        if (!authManager.isLoggedIn() && !name.equals("login") && !name.equals("register")) return "You need to be logged in!";
        
        lock.lock();
        Main.logger.log(Level.INFO, "Running \"" + name + "\" command");
        String res = run();
        lock.unlock();
        return res;
    }
}
