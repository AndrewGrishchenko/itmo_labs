package lab5.commands;

import lab5.utility.console.Console;

public class Exit extends Command {
    private final Console console;

    public Exit(Console console) {
        super("exit", "завершить программу (без сохранения в файл)", "'exit'");
        this.console = console;
    }

    @Override
    public boolean run(String[] args) {
        console.println("Завершение программы...");
        return true;
    }
}
