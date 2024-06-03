package lab7_server.commands;

import lab7_server.managers.AuthManager;

public class Register extends Command {
    private AuthManager authManager;

    public Register (AuthManager authManager) {
        super("register", "зарегистрировать аккаунт", "'register <username> <?password>'");
        this.authManager = authManager;
    }

    @Override
    public String run() {
        String args[] = getArgs();

        if (args.length == 2) {
            return authManager.register(args[1], "");
        } else {
            return authManager.register(args[1], args[2]);
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 4 && getArgs().length != 2) return getUsage();
        return null;
    }
}
