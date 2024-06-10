package lab7_server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab7_core.exceptions.InvalidDataException;
import lab7_server.commands.Clear;
import lab7_server.commands.CommandManager;
import lab7_server.commands.ExecuteScript;
import lab7_server.commands.Exit;
import lab7_server.commands.FilterGreaterThanEvent;
import lab7_server.commands.Help;
import lab7_server.commands.Info;
import lab7_server.commands.Insert;
import lab7_server.commands.Login;
import lab7_server.commands.Logout;
import lab7_server.commands.PrintFieldDescendingEvent;
import lab7_server.commands.Register;
import lab7_server.commands.RemoveAnyByEvent;
import lab7_server.commands.RemoveKey;
import lab7_server.commands.RemoveLower;
import lab7_server.commands.RemoveLowerKey;
import lab7_server.commands.ReplaceIfLower;
import lab7_server.commands.Show;
import lab7_server.commands.Update;
import lab7_server.managers.CollectionManager;
import lab7_server.managers.DBManager;
import lab7_server.managers.TCPServer;

public class Main {
    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            DBManager.init();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error");
            System.exit(1);
        }
        
        CollectionManager collectionManager = new CollectionManager();
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
            addCommand(new Login());
            addCommand(new Register());
            addCommand(new Logout());
        }};
        commandManager.addCommand(new ExecuteScript(commandManager));
        commandManager.addCommand(new Help(commandManager));

        Reader reader = new InputStreamReader(System.in);

        new TCPServer(4004, collectionManager, commandManager, reader).run();
    }
}