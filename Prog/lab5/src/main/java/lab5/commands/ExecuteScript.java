package lab5.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import lab5.adapters.ConsoleAdapter;
import lab5.adapters.ScannerAdapter;
import lab5.exceptions.IncompleteScriptRuntimeException;
import lab5.exceptions.ScriptProcessingException;
import lab5.managers.CommandManager;
import lab5.models.ExitCode;

/**
 * Команда 'execute_script'. Запускает скрипт из файла
 */
public class ExecuteScript extends Command {
    private static ArrayList<String> runningScripts = new ArrayList<>();
    private CommandManager commandManager;
    
    /**
     * Конструктор команды
     * @param commandManager менеджер команд
     * @see CommandManager
     */
    public ExecuteScript (CommandManager commandManager) {
        super("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме", "'execute_script <fileName>'");
        this.commandManager = commandManager;
    }

    /**
     * Проверяет наличие заднного скрипта среди запущенных
     * @param fileName имя файла скрипта для проверки
     * @return возвращает true, если скрипт уже есть среди запущенных, false в противном случае
     */
    public static boolean containsScript(String fileName) {
        for (String script : runningScripts) {
            if (script.equals(fileName)) return true;
        }
        return false;
    }

    /**
     * Добавляет скрипт в список запущенных
     * @param fileName имя файла скрипта
     */
    public static void addRunningScript(String fileName) {
        runningScripts.add(fileName);
    }

    /**
     * Удаляет последний запущенный скрипт из списка
     */
    public static void removeLastRunningScript() {
        runningScripts.remove(runningScripts.size() - 1);
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public ExitCode run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            String fileName = args[1];

            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            ScannerAdapter.setFileMode(inputStreamReader);
            ExecuteScript.addRunningScript(fileName);

            String[] userInput;
            while (ScannerAdapter.hasNext()) {
                userInput = ScannerAdapter.getUserInput()[0].split(" ");

                if (userInput[0].equals("execute_script")) {
                    if (ExecuteScript.containsScript(userInput[1])) {
                        ConsoleAdapter.printErr("запрет рекурсии!");
                        ScannerAdapter.setInteractiveMode();
                        ExecuteScript.removeLastRunningScript();
                        return ExitCode.ERROR;
                    }
                }

                ExitCode exitCode = commandManager.invokeCommand(userInput);
                if (exitCode == ExitCode.ERROR) {
                    throw new ScriptProcessingException(userInput[0]);
                } else if (exitCode == ExitCode.EXIT) {
                    ExecuteScript.removeLastRunningScript();
                    ConsoleAdapter.println("Выполнение скрипта завершено!");
                    return exitCode;
                }
            }

            ExecuteScript.removeLastRunningScript();
            if (ExecuteScript.runningScripts.size() == 0) ScannerAdapter.setInteractiveMode();
            ConsoleAdapter.println("Выполнение скрипта завершено!");
            return ExitCode.OK;
        } catch (FileNotFoundException e) {
            ConsoleAdapter.printErr("файл не найден!");
        } catch (ScriptProcessingException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (IncompleteScriptRuntimeException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        ScannerAdapter.setInteractiveMode();
        ConsoleAdapter.println("Выполнение скрипта остановлено!");
        return ExitCode.ERROR;
    }
}
