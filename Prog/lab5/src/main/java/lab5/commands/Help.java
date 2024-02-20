package lab5.commands;

import java.util.ArrayList;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CommandManager;
import lab5.utility.Runner.ExitCode;

public class Help extends Command {
    private CommandManager commandManager;
    
    public Help (CommandManager commandManager) {
        super("help", "вывести справку по доступным командам", "'help'");
        this.commandManager = commandManager;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }
        
        // String message = "help: вывести справку по доступным командам\n"
        // + "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n"
        // + "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n"
        // + "insert null {element}: добавить новый элемент с заданным ключом\n"
        // + "update id {element}: обновить значение элемента коллекции, id которого равен заданному\n"
        // + "remove_key null: удалить элемент из коллекции по его ключу\n"
        // + "clear: очистить коллекцию\n"
        // + "save: сохранить коллекцию в файл\n"
        // + "execute_script file_name: считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n"
        // + "exit: завершить программу (без сохранения в файл)\n"
        // + "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный\n"
        // + "replace_if_lowe null {element}: заменить значение по ключу, если новое значение меньше старого\n"
        // + "remove_lower_key null: удалить из коллекции все элементы, ключ которых меньше, чем заданный\n"
        // + "remove_any_by_event event: удалить из коллекции один элемент, значение поля event которого эквивалентно заданному\n"
        // + "filter_greater_than_event event: вывести элементы, значение поля event которых больше заданного\n"
        // + "print_field_descending_event: вывести значения поля event всех элементов в порядке убывания\n";
        
        String message = "";
        ArrayList<Command> commands = commandManager.getCommands();
        for (int i = 0; i < commands.size(); i++) {
            message += commands.get(i).getInfo() + "\n";
        }

        ConsoleAdapter.println(message);
        return ExitCode.OK;
    }
}
