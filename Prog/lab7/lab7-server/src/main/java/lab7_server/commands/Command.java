package lab7_server.commands;

import java.util.Objects;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import lab7_core.models.CommandMeta;
import lab7_server.Main;
import lab7_server.interfaces.Validatable;
import lab7_server.managers.AuthManager;

public abstract class Command extends RecursiveTask<String> implements Validatable {
    private final CommandMeta meta;

    private AuthManager authManager;
    
    private String[] args;
    private Object obj;

    private ReentrantLock lock;

    public Command (String name, String description, String usage, boolean authRequired, int... argC) {
        this.meta = new CommandMeta(name, description, usage, null, authRequired, argC);
    }

    public Command (String name, String description, String usage, String requiredObject, int... argC) {
        this.meta = new CommandMeta(name, description, usage, requiredObject, true, argC);
    }

    public String getName () {
        return meta.getName();
    }

    public String getDescription () {
        return meta.getDescription();
    }

    public String getUsage () {
        return "Использование: " + meta.getUsage();
    }

    public String getRequiredObject () {
        return meta.getRequiredObject();
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

    public CommandMeta getMeta () {
        return meta;
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
        return Objects.equals(meta, other.meta);
    }

    @Override
    public String toString () {
        return meta.getName() + ": " + meta.getDescription();
    }

    @Override
    public String compute () {
        if (meta.isAuthRequired() && !authManager.isLoggedIn()) return "You need to be logged in!";

        lock.lock();
        Main.logger.log(Level.INFO, "Running \"" + meta.getName() + "\" command");
        String res = run();
        lock.unlock();
        return res;
    }

    @Override
    public String isValid() {
        if (!this.getMeta().testArgC(getArgs().length)) return getUsage();
        return null;
    }
}
