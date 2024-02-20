package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.utility.Runner.ExitCode;

public class Info extends Command {
    private CollectionManager collectionManager;
    private String fileName;

    public Info(CollectionManager collectionManager, String fileName) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)", "'info'");
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        String message = "Информация о коллекции:\n  Тип: " + collectionManager.getType()
        + "\n  Дата инициализации: " + collectionManager.getInitTime()
        + "\n  Дата последнего изменения: " + collectionManager.getLastUpdateTime()
        + "\n  Количество элементов: " + collectionManager.getSize()
        + "\n  Название xml файла: " + fileName + "\n";

        ConsoleAdapter.println(message);
        return ExitCode.OK;
    }
}
