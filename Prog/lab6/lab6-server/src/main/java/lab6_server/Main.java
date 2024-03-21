package lab6_server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab6_core.adapters.ConsoleAdapter;
import lab6_core.exceptions.InvalidDataException;
import lab6_server.commands.Clear;
import lab6_server.commands.CommandManager;
import lab6_server.commands.ExecuteScript;
import lab6_server.commands.Exit;
import lab6_server.commands.FilterGreaterThanEvent;
import lab6_server.commands.Help;
import lab6_server.commands.Info;
import lab6_server.commands.Insert;
import lab6_server.commands.PrintFieldDescendingEvent;
import lab6_server.commands.RemoveAnyByEvent;
import lab6_server.commands.RemoveKey;
import lab6_server.commands.RemoveLower;
import lab6_server.commands.RemoveLowerKey;
import lab6_server.commands.ReplaceIfLower;
import lab6_server.commands.Show;
import lab6_server.commands.Update;
import lab6_server.managers.CollectionManager;
import lab6_server.managers.TCPServer;

public class Main {
    public static void main(String[] args) {
        final String fileName = "test1.xml";
        CollectionManager collectionManager = new CollectionManager(fileName);
        try {
            collectionManager.dumpData();
        } catch (FileNotFoundException e) {
            ConsoleAdapter.printErr("файл не найден!");
        } catch (JsonProcessingException e) {
            ConsoleAdapter.printErr("ошибка парсинга xml! Проверьте валидность данных");
        } catch (IOException e) {
            ConsoleAdapter.printErr("ошибка ввода/вывода!");
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        CommandManager commandManager = new CommandManager() {{
            addCommand(new Exit());
            addCommand(new Show(collectionManager));
            addCommand(new Clear(collectionManager));
            addCommand(new RemoveKey(collectionManager));
            addCommand(new Insert(collectionManager));
            addCommand(new Update(collectionManager));
            addCommand(new RemoveLower(collectionManager));
            addCommand(new ReplaceIfLower(collectionManager));
            addCommand(new RemoveLowerKey(collectionManager));
            addCommand(new RemoveAnyByEvent(collectionManager));
            addCommand(new FilterGreaterThanEvent(collectionManager));
            addCommand(new PrintFieldDescendingEvent(collectionManager));
            addCommand(new Info(collectionManager));
        }};
        commandManager.addCommand(new ExecuteScript(commandManager));
        commandManager.addCommand(new Help(commandManager));

        // new Thread(() -> {
        //     Reader reader = new InputStreamReader(System.in);
        //     Scanner scanner = new Scanner(reader);
        //     System.out.println("Type \"save\" to save or \"exit\" to exit");
        //     while (true) {
        //         try {
        //             if (reader.ready()) {
        //                 if (scanner.hasNext()) {
        //                     String line = scanner.nextLine();
        //                     if (line.equals("save")) {
        //                         System.out.println("saving...");
        //                         collectionManager.saveData();
        //                         System.out.println("saved!");
        //                     } else if (line.equals("exit")) {
        //                         System.out.println("saving...");
        //                         collectionManager.saveData();
        //                         System.out.println("saved!");
        //                         System.exit(0);
        //                     }
        //                 }
        //             }
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //             scanner.close();
        //             System.exit(1);
        //         }
        //     }
        // }).start();
        Reader reader = new InputStreamReader(System.in);

        new TCPServer(4004, collectionManager, commandManager, reader).run();
    }
}