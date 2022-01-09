public class Main {
    public static void main(String[] args) {
        CommandManager commandManager = CommandManager.getInstance();

        generateCommands();

        commandManager.waitForInput();
    }

    public static void generateCommands() {
        CommandManager commandManager = CommandManager.getInstance();
        MoveManager moveManager = MoveManager.getInstance();

        commandManager.commands.addCommand("xboard", new RunnableFunction() {
            @Override
            public void run(String response) {
                commandManager.send("xboard recieved!");
            }
        });
        commandManager.commands.addCommand("protover [0-9]+", new RunnableFunction() {
            @Override
            public void run(String response) {
                commandManager.send("feature sigint=0 san=0 name=\"Chessmate\"");
            }
        });
        commandManager.commands.addCommand("[a-h][1-8][a-h][1-8].?", new RunnableFunction() {
            @Override
            public void run(String response) {
                moveManager.receiveMove(response);
            }
        });
        commandManager.commands.addCommand("force", new RunnableFunction() {
            @Override
            public void run(String response) {
                moveManager.force();
            }
        });
        commandManager.commands.addCommand("go", new RunnableFunction() {
            @Override
            public void run(String response) {
                moveManager.go();
            }
        });
        commandManager.commands.addCommand("quit", new RunnableFunction() {
            @Override
            public void run(String response) {
                System.exit(0);
            }
        });
        commandManager.commands.addCommand("new", new RunnableFunction() {
            @Override
            public void run(String response) {
                moveManager.newGame();
            }
        });
    }
}
