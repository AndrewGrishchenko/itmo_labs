package lab6_server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

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
    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        final String fileName = "test1.xml";
        CollectionManager collectionManager = new CollectionManager(fileName);
        try {
            collectionManager.dumpData();
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Файл не найден!");
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Ошибка парсинга xml! Проверьте валидность данных");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода/вывода!");
        } catch (InvalidDataException e) {
            logger.log(Level.SEVERE, e.getMessage());
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

        Reader reader = new InputStreamReader(System.in);

        new TCPServer(4004, collectionManager, commandManager, reader).run();
    }
}