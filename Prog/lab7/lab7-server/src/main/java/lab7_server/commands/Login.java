package lab7_server.commands;

public class Login extends Command {
    public Login () {
        super("login", "авторизоваться в аккаунт", "'login <username> <?password>'", false, 2, 3);
    }

    @Override
    public String run() {
        String args[] = getArgs();

        if (args.length == 2) {
            return getAuthManager().login(args[1], "");
        } else {
            return getAuthManager().login(args[1], args[2]);
        }
    }
}
