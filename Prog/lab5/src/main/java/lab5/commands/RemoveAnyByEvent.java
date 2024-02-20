package lab5.commands;

import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.utility.Runner.ExitCode;

public class RemoveAnyByEvent extends Command {
    private CollectionManager collectionManager;

    public RemoveAnyByEvent(CollectionManager collectionManager) {
        super("remove_any_by_event", "удалить из коллекции один элемент, значение поля event которого эквивалентно заданному", "'remove_any_by_event'");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            Event event = new Event();
            event.fillData();
            
            if (!event.validate()) {
                throw new InvalidDataException("Ивент имеет невалидные данные!");
            }
            
            if (collectionManager.removeOneByEvent(event)) {
                ConsoleAdapter.println("Тикет с данным ивентом был удален!");
            }
            else {
                ConsoleAdapter.println("Данный ивент нигде не используется; ни один тикет не удален");
            }
            return ExitCode.OK;
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (TooManyArgumentsException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("данные должны являться числом!");
        } catch (IllegalArgumentException e) {
            ConsoleAdapter.printErr("Введенные данные неверны!");
        } catch (DateTimeParseException e) {
            ConsoleAdapter.printErr("ошибка формата даты!");
        } catch (ZoneRulesException e) {
            ConsoleAdapter.printErr("ошибка формата зоны!");
        }

        return ExitCode.ERROR;
    }
}
