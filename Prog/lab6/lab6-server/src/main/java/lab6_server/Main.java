package lab6_server;

import java.io.FileNotFoundException;
import java.io.IOException;

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
import lab6_server.commands.Save;
import lab6_server.commands.Show;
import lab6_server.commands.Update;
import lab6_server.managers.CollectionManager;
import lab6_server.managers.TCPServer;

public class Main {
    public static void main(String[] args) {
        CollectionManager collectionManager = new CollectionManager();
        
        final String fileName = "test1.xml";
        try {
            collectionManager.dumpData(fileName);
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
            addCommand(new Save(collectionManager, fileName));
            addCommand(new Insert(collectionManager));
            addCommand(new Update(collectionManager));
            addCommand(new RemoveLower(collectionManager));
            addCommand(new ReplaceIfLower(collectionManager));
            addCommand(new RemoveLowerKey(collectionManager));
            addCommand(new RemoveAnyByEvent(collectionManager));
            addCommand(new FilterGreaterThanEvent(collectionManager));
            addCommand(new PrintFieldDescendingEvent(collectionManager));
            addCommand(new Info(collectionManager, fileName));
        }};
        commandManager.addCommand(new ExecuteScript(commandManager));
        commandManager.addCommand(new Help(commandManager));

        new TCPServer(4004, commandManager).run();
    }
}