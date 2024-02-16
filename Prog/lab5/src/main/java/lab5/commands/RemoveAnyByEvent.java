package lab5.commands;

import java.time.format.DateTimeParseException;

import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.utility.console.Console;

public class RemoveAnyByEvent extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public RemoveAnyByEvent(Console console, CollectionManager collectionManager) {
        super("remove_any_by_event", "удалить из коллекции один элемент, значение поля event которого эквивалентно заданному", "'remove_any_by_event'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            console.println(getUsage());
            return false;
        }

        try {
            Event event = new Event();
            event.fillData(console);
            
            if (!event.validate()) {
                throw new InvalidDataException("Ивент имеет невалидные данные!");
            }
            
            if (collectionManager.removeOneByEvent(event)) {
                console.println("Тикет с данным ивентом был удален!");
            }
            else {
                console.println("Данный ивент нигде не используется; ни один тикет не удален");
            }
            return true;
        } catch (InvalidDataException e) {
            console.printErr(e.getMessage());
        } catch (TooManyArgumentsException e) {
            console.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            console.printErr("данные должны являться числом!");
        } catch (IllegalArgumentException e) {
            console.printErr("Введенные данные неверны!");
        } catch (DateTimeParseException e) {
            console.printErr("ошибка формата даты!");
        }

        return false;
    }
}