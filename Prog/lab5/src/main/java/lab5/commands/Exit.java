package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.utility.Runner.ExitCode;

public class Exit extends Command {
    public Exit() {
        super("exit", "завершить программу (без сохранения в файл)", "'exit'");
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }
        
        ConsoleAdapter.println("Завершение программы...");
        return ExitCode.EXIT;
    }
}
