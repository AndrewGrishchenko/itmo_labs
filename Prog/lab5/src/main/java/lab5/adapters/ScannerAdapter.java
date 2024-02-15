package lab5.adapters;

import java.util.Scanner;

import lab5.exceptions.TooManyArgumentsException;
import lab5.models.Coordinates;
import lab5.models.Event;
import lab5.models.ScanMode;
import lab5.models.Ticket;
import lab5.models.TicketType;
import lab5.utility.console.Console;

public class ScannerAdapter {
    private static Scanner userScanner;
    private static ScanMode mode = ScanMode.SINGLE;

    public static Scanner getScanner() {
        return userScanner;
    }

    public static void setScanner(Scanner userScanner) {
        ScannerAdapter.userScanner = userScanner;
    }

    public static String[] getUserInput() {
        return userScanner.nextLine().trim().split(" ");
    }

    public static Ticket buildTicket(Console console, Ticket ticket) throws TooManyArgumentsException {
        console.print("Введите name: ");
        ticket.setName(getString());

        Coordinates coordinates = new Coordinates();
        console.print("Введите x: ");
        coordinates.setX(getDouble());

        console.print("Введите y: ");
        coordinates.setY(getDouble());
        ticket.setCoordinates(coordinates);

        console.print("Введите price: ");
        ticket.setPrice(getInt());

        console.print("Введите type: ");
        ticket.setType(TicketType.valueOf(getString()));

        Event event = new Event();
        console.print("Введите name: ");
        event.setName(getString());

        console.print("Введите date: ");
        event.setDate(getString());

        console.print("Введите ticketsCount: ");
        event.setTicketsCount(getLong());
        
        console.print("Введите description: ");
        event.setDescription(getString());
        ticket.setEvent(event);
        return ticket;
    }

    public static String getString() {
        return String.join(" ", getUserInput());
    }

    public static Double getDouble() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Double.parseDouble(userInput[0]);
    }

    public static int getInt() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Integer.parseInt(userInput[0]);
    }

    public static Long getLong() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Long.parseLong(userInput[0]);
    }

    public static ScanMode getScanMode() {
        return mode;
    }

    public static void setSingleMode() {
        mode = ScanMode.SINGLE;
    }

    public static void setMultiMode() {
        mode = ScanMode.MULTI;
    }
}
