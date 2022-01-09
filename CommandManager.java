import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandManager {
    String log;
    Scanner scanner;
    Commands commands;
    private static CommandManager instance = null;

    private CommandManager() {
        log = "";
        scanner = new Scanner(System.in);
        commands = new Commands();
    }

    public static CommandManager getInstance() {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }

    public void send(String commandMessage) {
        System.out.println(commandMessage);
    }

    public void waitForInput() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            log = log + line + "\n";

            // For every line read, check if the line matches a known command
            for (Map.Entry<String, RunnableFunction> entry : commands.commands.entrySet()) {
                if (line.matches(entry.getKey()))
                    entry.getValue().run(line);
            }
        }
    }

    public static class Commands {
        Map<String, RunnableFunction> commands;

        public Commands() {
            commands = new HashMap<>();
        }

        public void addCommand(String command, RunnableFunction function) {
            commands.put(command, function);
        }
    }
}

abstract class RunnableFunction {
    abstract public void run(String response);
}
