package lab6_server.commands;

import java.util.ArrayList;
import java.util.Arrays;

import lab6_core.models.Event;
import lab6_core.models.Script;
import lab6_core.models.Scripts;
import lab6_core.models.Ticket;
import lab6_core.models.ValueChecker;

/**
 * Команда 'execute_script'. Запускает скрипт из файла
 */
public class ExecuteScript extends Command {
    private ArrayList<String> runningScripts = new ArrayList<>();
    private CommandManager commandManager;

    private Scripts scripts;

    private String response = "";
    private boolean isFilling = false;
    private String fillType;

    private Object model;
    private Command command;
    
    /**
     * Конструктор команды
     * @param commandManager менеджер команд
     * @see CommandManager
     */
    public ExecuteScript (CommandManager commandManager) {
        super("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме", "'execute_script <fileName>'", "script");
        this.commandManager = commandManager;
    }

    /**
     * Проверяет наличие заднного скрипта среди запущенных
     * @param fileName имя файла скрипта для проверки
     * @return возвращает true, если скрипт уже есть среди запущенных, false в противном случае
     */
    public boolean containsScript(String fileName) {
        for (String script : runningScripts) {
            if (script.equals(fileName)) return true;
        }
        return false;
    }

    /**
     * Добавляет скрипт в список запущенных
     * @param fileName имя файла скрипта
     */
    public void addRunningScript(String fileName) {
        runningScripts.add(fileName);
    }

    /**
     * Возвращает название текущего запущенного скрипта
     * @return название текущего запущенного скрипта
     */
    public String getRunningScript() {
        return runningScripts.get(runningScripts.size() - 1);
    }

    /**
     * Удаляет последний запущенный скрипт из списка
     */
    public void removeLastRunningScript() {
        runningScripts.remove(runningScripts.size() - 1);
    }

    private boolean fillModel (String[] line) {
        ValueChecker result = new ValueChecker(false);
        switch (fillType) {
            case "ticket":
                if (model == null) model = new Ticket();
                result = ((Ticket) model).fillPartly(line);
                break;
            case "event":
                if (model == null) model = new Event();
                ((Event) model).fillPartly(line);
                break;
        }

        response += result.getMessage() != null ? result.getMessage() : "";

        response += String.join(" ", line) + "\n";

        response += result.getError() != null ? "ОШИБКА: " + result.getError() + "\n" : "";

        return result.getCheck();
    }

    private void handleInput (String input) {
        response += "(" + getArgs()[1] + ")> ";

        String[] line = input.split(" ");
        
        if (isFilling) {
            if (fillModel(line)) {
                isFilling = false;
                command.setObj(model);

                response += command.run() + "\n";
            }

            return;
        }

        response += input + "\n";

        command = commandManager.getCommand(line[0]);

        if (command == null) {
            response += "Команда \"" + line[0] + "\" не найдена!";
            return;
        }

        command.setArgs(line);

        if (command.getRequiredObject() != null) {
            if (command.getName().equals("execute_script")) {
                if (containsScript(line[1])) {
                    response += "ОШИБКА: Запрет рекурсии! Скрипт " + line[1] + " не был запущен\n";
                    return;
                }

                if (line.length != 2) {
                    response += getUsage() + "\n";
                    return;
                }

                handleScript(scripts.findScript(line[1]));
                return;
            }

            isFilling = true;
            fillType = command.getRequiredObject();
            return;
        }

        if (command.isValid() == null) {
            response += command.run() + "\n";
        } else {
            response += command.isValid() + "\n";
        }
    }

    public void handleScript (Script script) {
        addRunningScript(script.getFileName());
        Arrays.stream(script.getContent()).forEach(line -> handleInput(line));
        response += "Выполнение скрипта " + script.getFileName() + " завершено!\n";
        removeLastRunningScript();
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        // System.out.println("FROM EXECUTE GOT " + getArgs()[1]);
        // System.out.println(((String) getObj()));
        
        // String fileName = getArgs()[1];
        // String[] commands = ((String) getObj()).split("\n");
        // String outMessage = "";

        // for (String command : commands) {
        //     outMessage += "(" + fileName + ")> " + command + "\n";
        // }

        scripts = (Scripts) getObj();
        // Arrays.stream(scripts.getScripts()..getContent()).forEach(line -> handleInput(line));
        
        // scripts.getScripts().stream().forEach(script -> handleScript(script));
        // Arrays.stream(scripts.getPrimaryScript().getContent()).forEach(line -> handleInput(line));
        handleScript(scripts.getPrimaryScript());

        // Arrays.stream(((String) getObj()).split("\n")).forEach(line -> handleInput(line));

        return response;

        // try {
        //     addRunningScript(fileName);
        // }

        // String[] args = getArgs();
        // if (args.length != 2) {
        //     return getUsage();
        // }

        // try {
        //     String fileName = args[1];

        //     FileInputStream fileInputStream = new FileInputStream(fileName);
        //     InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        //     ScannerAdapter.setFileMode(inputStreamReader);
        //     ExecuteScript.addRunningScript(fileName);

        //     String[] userInput;
        //     while (ScannerAdapter.hasNext()) {
        //         userInput = ScannerAdapter.getUserInput()[0].split(" ");

        //         if (userInput[0].equals("execute_script")) {
        //             if (ExecuteScript.containsScript(userInput[1])) {
        //                 ConsoleAdapter.printErr("Запрет рекурсии! Выполнение скрипта " + ExecuteScript.getRunningScript() + " остановлено!");
        //                 ScannerAdapter.setInteractiveMode();
        //                 ExecuteScript.removeLastRunningScript();
        //                 return ExitCode.ERROR;
        //             }
        //         }

        //         ExitCode exitCode = commandManager.invokeCommand(userInput);
        //         if (exitCode == ExitCode.ERROR) {
        //             throw new ScriptProcessingException(userInput[0]);
        //         } else if (exitCode == ExitCode.EXIT) {
        //             ConsoleAdapter.println("Выполнение скрипта " + ExecuteScript.getRunningScript() + " завершено!");
        //             ExecuteScript.removeLastRunningScript();
        //             return exitCode;
        //         }
        //     }

        //     ConsoleAdapter.println("Выполнение скрипта " + ExecuteScript.getRunningScript() + " завершено!");
        //     ExecuteScript.removeLastRunningScript();
        //     if (ExecuteScript.runningScripts.size() == 0) ScannerAdapter.setInteractiveMode();
        //     return ExitCode.OK;
        // } catch (FileNotFoundException e) {
        //     ConsoleAdapter.printErr("файл не найден!");
        // } catch (ScriptProcessingException e) {
        //     ConsoleAdapter.printErr(e.getMessage());
        // } catch (IncompleteScriptRuntimeException e) {
        //     ConsoleAdapter.printErr(e.getMessage());
        // }

        // ScannerAdapter.setInteractiveMode();
        // ConsoleAdapter.println("Выполнение скрипта " + ExecuteScript.getRunningScript() + " остановлено!");
        // return ExitCode.ERROR;
        // return "";
    }

    @Override
    public String isValid() {
        if (getArgs().length != 2) return getUsage();
        return null;
    }
}
