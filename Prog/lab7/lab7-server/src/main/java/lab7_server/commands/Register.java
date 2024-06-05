package lab7_server.commands;

public class Register extends Command {
    public Register () {
        super("register", "зарегистрировать аккаунт", "'register <username> <?password>'", false, 2, 3);
    }

    @Override
    public String run() {
        String args[] = getArgs();

        if (args.length == 2) {
            return getAuthManager().register(args[1], "");
        } else {
            return getAuthManager().register(args[1], args[2]);
        }
    }
}
