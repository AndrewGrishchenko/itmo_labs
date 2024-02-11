package lab5;

import java.io.IOException;
import java.util.Scanner;

import lab5.adapters.ScannerAdapter;
import lab5.commands.Exit;
import lab5.commands.Help;
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

        CommandManager commandManager = new CommandManager() {{
            addCommand(new Exit(console));
            addCommand(new Help(console));
            addCommand(new Show(console, collectionManager));
        }};

        //TODO: check args length and throw exception
        String fileName = "/home/andrew/itmo_labs/Prog/lab5/src/main/java/lab5/test1.xml";
        try {
            collectionManager.dumpData(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // for (int i = 0; i < collectionManager.getCollection().size(); i++) {
        //     console.println(collectionManager.getCollection().get(i+1).toString());
        // }
        
        new Runner(console, collectionManager, commandManager).interactiveMode();
    }
}
