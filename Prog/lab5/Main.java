import java.util.Scanner;

import adapters.ScannerAdapter;
import commands.Exit;
import managers.CollectionManager;
import managers.CommandManager;
import utility.Runner;
import utility.console.StandardConsole;

public class Main {
    public static void main(String[] args) {
        StandardConsole console = new StandardConsole();
        CollectionManager collectionManager = new CollectionManager();
        ScannerAdapter.setScanner(new Scanner(System.in));

        CommandManager commandManager = new CommandManager() {{
            addCommand(new Exit(console));
        }};

        new Runner(console, commandManager).interactiveMode();
    }
}
