package lab5;

import java.util.Scanner;

import lab5.adapters.ScannerAdapter;
import lab5.commands.Clear;
import lab5.commands.Exit;
import lab5.commands.Help;
import lab5.commands.Insert;
import lab5.commands.RemoveKey;
import lab5.commands.Save;
import lab5.commands.Show;
import lab5.managers.CollectionManager;
import lab5.managers.CommandManager;
import lab5.utility.Runner;
import lab5.utility.console.StandardConsole;

public class Main {
    public static void main(String[] args) {
        final StandardConsole console = new StandardConsole();
        final CollectionManager collectionManager = new CollectionManager();
        ScannerAdapter.setScanner(new Scanner(System.in));

        //TODO: check args length and throw exception
        final String fileName = "/home/andrew/itmo_labs/Prog/lab5/src/main/java/lab5/test1.xml";
        try {
            collectionManager.dumpData(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommandManager commandManager = new CommandManager() {{
            addCommand(new Exit(console));
            addCommand(new Help(console));
            addCommand(new Show(console, collectionManager));
            addCommand(new Clear(console, collectionManager));
            addCommand(new RemoveKey(console, collectionManager));
            addCommand(new Save(console, collectionManager, fileName));
            addCommand(new Insert(console, collectionManager));
        }};
        
        new Runner(console, collectionManager, commandManager).interactiveMode();
    }
}
