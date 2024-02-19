package lab5;

import java.util.Scanner;

import lab5.adapters.ScannerAdapter;
import lab5.commands.Clear;
import lab5.commands.ExecuteScript;
import lab5.commands.Exit;
import lab5.commands.FilterGreaterThanEvent;
import lab5.commands.Help;
import lab5.commands.Info;
import lab5.commands.Insert;
import lab5.commands.PrintFieldDescendingEvent;
import lab5.commands.RemoveAnyByEvent;
import lab5.commands.RemoveKey;
import lab5.commands.RemoveLower;
import lab5.commands.RemoveLowerKey;
import lab5.commands.ReplaceIfLower;
import lab5.commands.Save;
import lab5.commands.Show;
import lab5.commands.Update;
import lab5.managers.CollectionManager;
import lab5.managers.CommandManager;
import lab5.models.ScanMode;
import lab5.utility.Runner;
import lab5.utility.console.StandardConsole;

public class Main {
    public static void main(String[] args) {
        final StandardConsole console = new StandardConsole();
        final CollectionManager collectionManager = new CollectionManager();
        ScannerAdapter.addScanner(ScanMode.INTERACTIVE, new Scanner(System.in));

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
            addCommand(new Update(console, collectionManager));
            addCommand(new RemoveLower(console, collectionManager));
            addCommand(new ReplaceIfLower(console, collectionManager));
            addCommand(new RemoveLowerKey(console, collectionManager));
            addCommand(new RemoveAnyByEvent(console, collectionManager));
            addCommand(new FilterGreaterThanEvent(console, collectionManager));
            addCommand(new PrintFieldDescendingEvent(console, collectionManager));
            addCommand(new Info(console, collectionManager, fileName));
        }};
        commandManager.addCommand(new ExecuteScript(console, collectionManager, commandManager));
        
        new Runner(console, collectionManager, commandManager).interactiveMode();
    }
}
