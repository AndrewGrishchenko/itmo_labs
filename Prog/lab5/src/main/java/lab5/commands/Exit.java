package lab5.commands;

import lab5.adapters.ConsoleAdapter;

public class Exit extends Command {
    public Exit() {
        super("exit", "завершить программу (без сохранения в файл)", "'exit'");
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return false;
        }
        
        ConsoleAdapter.println("Завершение программы...");
        return true;
    }
}
