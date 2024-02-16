package lab5.commands;

import lab5.managers.CollectionManager;
import lab5.utility.console.Console;

public class Info extends Command {
    private Console console;
    private CollectionManager collectionManager;
    private String fileName;

    public Info(Console console, CollectionManager collectionManager, String fileName) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)", "'info'");
        this.console = console;
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            console.println(getUsage());
            return false;
        }

        String message = "Информация о коллекции:\n  Тип: " + collectionManager.getType()
        + "\n  Дата инициализации: " + collectionManager.getInitTime()
        + "\n  Дата последнего изменения: " + collectionManager.getLastUpdateTime()
        + "\n  Количество элементов: " + collectionManager.getSize()
        + "\n  Название xml файла: " + fileName + "\n";

        console.println(message);
        return true;
    }
}
