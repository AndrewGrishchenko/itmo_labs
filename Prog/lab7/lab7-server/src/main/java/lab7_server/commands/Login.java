package lab7_server.commands;

import lab7_server.managers.AuthManager;

public class Login extends Command {
    private AuthManager authManager;
    
    public Login (AuthManager authManager) {
        super("login", "авторизоваться в аккаунт", "'login <username> <?password>'");
        this.authManager = authManager;
    }

    @Override
    public String run() {
        String args[] = getArgs();

        if (args.length == 2) {
            return authManager.login(args[1], "");    
        } else {
            return authManager.login(args[1], args[2]);
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 3 && getArgs().length != 2) return getUsage();
        return null;
    }
}
