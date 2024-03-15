package lab6_server.managers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import lab6_core.models.Message;
import lab6_server.commands.Command;
import lab6_server.commands.CommandManager;

public class TCPServer implements Runnable {
    private Socket clientSocket;
    private ServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private CommandManager commandManager;

    private final int port;

    public TCPServer (int port, CommandManager commandManager) {
        this.port = port;
        this.commandManager = commandManager;
    }

    public void run () {
        try {
            server = new ServerSocket(port);
            System.out.println("server started");
            
            while (true) {
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

                    while (true) {
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
                    }

                    clientSocket.close();
                    in.close();
                    out.close();
                } catch (EOFException | SocketException e) {
                    System.out.println("disconnected");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
