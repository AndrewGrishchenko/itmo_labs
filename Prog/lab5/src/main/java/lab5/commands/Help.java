package lab5.commands;

import lab5.utility.console.Console;

public class Help extends Command {
    private final Console console;

    public Help (Console console) {
        super("help", "вывести справку по доступным командам", "'help'");
        this.console = console;
    }

    @Override
    public boolean run(String[] args) {
        String message = "help: вывести справку по доступным командам\n"
        + "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n"
        + "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n"
        + "insert null {element}: добавить новый элемент с заданным ключом\n"
        + "update id {element}: обновить значение элемента коллекции, id которого равен заданному\n"
        + "remove_key null: удалить элемент из коллекции по его ключу\n"
        + "clear: очистить коллекцию\n"
        + "save: сохранить коллекцию в файл\n"
        + "execute_script file_name: считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n"
        + "exit: завершить программу (без сохранения в файл)\n"
        + "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный\n"
        + "replace_if_lowe null {element}: заменить значение по ключу, если новое значение меньше старого\n"
        + "remove_lower_key null: удалить из коллекции все элементы, ключ которых меньше, чем заданный\n"
        + "remove_any_by_event event: удалить из коллекции один элемент, значение поля event которого эквивалентно заданному\n"
        + "filter_greater_than_event event: вывести элементы, значение поля event которых больше заданного\n"
        + "print_field_descending_event: вывести значения поля event всех элементов в порядке убывания\n";
        console.println(message);
        return true;
    }
}
