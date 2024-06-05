package lab7_server.commands;

public class Logout extends Command {
    public Logout() {
        super("logout", "выйти из аккаунта", "'logout'", true, 1);
    }

    @Override
    public String run() {
        getAuthManager().logout();
        return "You are now logged out";
    }
}
