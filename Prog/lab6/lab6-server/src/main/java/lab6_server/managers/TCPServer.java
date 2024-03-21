package lab6_server.managers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import lab6_core.models.Message;
import lab6_server.commands.Command;
import lab6_server.commands.CommandManager;

public class TCPServer implements Runnable {
    private Socket clientSocket;
    private ServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private CollectionManager collectionManager;
    private CommandManager commandManager;
    
    private Reader reader;
    private Scanner scanner;
    private boolean isRunning = true;

    private final int port;

    public TCPServer (int port, CollectionManager collectionManager, CommandManager commandManager, Reader reader) {
        this.port = port;
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
        this.reader = reader;
    }

    public void run () {
        try {
            server = new ServerSocket(port);
            System.out.println("server started");
            
            scanner = new Scanner(reader);

            while (isRunning) {
                try {
                    clientSocket = server.accept();
                    System.out.println("new connection");
                    //Further: connection manager extends Thread
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                    in = new ObjectInputStream(clientSocket.getInputStream());
                    
                    Message msg;
                    String header = "";

                    Message response;
                    Command command;
                    String[] commandArgs = new String[]{};

                    while (isRunning) {
                        if (clientSocket.getInputStream().available() > 0) {
                            Object obj = in.readObject();
                            if (obj == null) break;
                            msg = (Message) obj;
                            header = msg.getHeader();
                            
                            switch (header) {
                                case "ticket":
                                    command = commandManager.getCommand(commandArgs[0]);  
                                    command.setObj(msg.getObj());
                                    response = new Message("response", command.run());
                                    break;
                                case "event":
                                    command = commandManager.getCommand(commandArgs[0]);
                                    command.setObj(msg.getObj());
                                    response = new Message("response", command.run());
                                    break;
                                default:
                                    String[] userInput = msg.getCommand();
                                    commandArgs = userInput;
                                    command = commandManager.getCommand(userInput[0]);
                                    if (command == null) {
                                        response = new Message("response", "Команда не найдена!");
                                        break;
                                    }
                                    if (command.getName().equals("exit")) {
                                        response = new Message("exit", command.run());
                                        break;
                                    }
                                    
                                    command.setArgs(userInput);                                
                                    
                                    if (command.isValid() != null) {
                                        response = new Message("response", command.isValid());
                                    } else if (command.getRequiredObject() != null) {
                                        response = new Message(command.getRequiredObject());
                                    } else {
                                        response = new Message("response", command.run());
                                    }
                                    break;
                            }
                            out.writeObject(response);
                            out.flush();
                        } else if (reader.ready() && scanner.hasNext()) {
                            String line = scanner.nextLine();
                            if (line.equals("save")) {
                                System.out.println("saving...");
                                collectionManager.saveData();
                                System.out.println("saved!");
                            } else if (line.equals("exit")) {
                                System.out.println("saving...");
                                collectionManager.saveData();
                                System.out.println("saved!");
                                isRunning = false;
                            }
                        }
                    }
                    Message shutdownMessage = new Message("shutdown");
                    out.writeObject(shutdownMessage);
                    clientSocket.close();
                    in.close();
                    out.close();
                        
                } catch (EOFException | SocketException e) {
                    e.printStackTrace();
                    System.out.println("disconnected. saving...");
                    collectionManager.saveData();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
