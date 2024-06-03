package lab7_server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab7_core.exceptions.InvalidDataException;
import lab7_core.models.Coordinates;
import lab7_core.models.Event;
import lab7_core.models.Ticket;
import lab7_core.models.TicketType;
import lab7_core.models.User;
import lab7_server.commands.Clear;
import lab7_server.commands.CommandManager;
import lab7_server.commands.ExecuteScript;
import lab7_server.commands.Exit;
import lab7_server.commands.FilterGreaterThanEvent;
import lab7_server.commands.Help;
import lab7_server.commands.Info;
import lab7_server.commands.Insert;
import lab7_server.commands.Login;
import lab7_server.commands.PrintFieldDescendingEvent;
import lab7_server.commands.Register;
import lab7_server.commands.RemoveAnyByEvent;
import lab7_server.commands.RemoveKey;
import lab7_server.commands.RemoveLower;
import lab7_server.commands.RemoveLowerKey;
import lab7_server.commands.ReplaceIfLower;
import lab7_server.commands.Show;
import lab7_server.commands.Update;
import lab7_server.managers.AuthManager;
import lab7_server.managers.CollectionManager;
import lab7_server.managers.DBManager;
import lab7_server.managers.TCPServer;

public class Main {
    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        AuthManager authManager = new AuthManager();

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
            addCommand(new Login(authManager));
            addCommand(new Register(authManager));
        }};
        commandManager.addCommand(new ExecuteScript(commandManager));
        commandManager.addCommand(new Help(commandManager));

        Reader reader = new InputStreamReader(System.in);

        try {
            DBManager.init();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error");
            System.exit(1);
        }

        new TCPServer(4004, collectionManager, commandManager, reader).run();

        

        // AuthManager am = new AuthManager();
        // am.register("some0", "123");
        // am.login("some0", "12");
        // am.login("some0", "123");


        // String[] parts = "2020-01-01 22:12:12 Europe/Moscow".split(" ");
        // LocalDateTime ldt = LocalDateTime.parse(parts[0] + "T" + parts[1]);
        // ZonedDateTime zdt = ldt.atZone(ZoneId.of(parts[2]));
        // Event event = new Event("123", zdt, Long.valueOf(10), "desc");

        // DBManager.executeInsert("events", event);

        // Ticket ticket = new Ticket(1, "ticket", new Coordinates(1.1, 1.2), 5, TicketType.VIP, event);
        // ticket.setCreatorId(1);

        // DBManager.findEvent(event);
        // DBManager.executeInsert("tickets", ticket);

        // ArrayList<Object> events = DBManager.executeSelect("events");
        // events.forEach((Object obj) -> {
        //     System.out.println(((Event) obj).toString());
        // });

        // ArrayList<Object> tickets = DBManager.executeSelect("tickets");
        // tickets.forEach((Object obj) -> {
        //     System.out.println(((Ticket) obj).toString());
        // });
    }
}