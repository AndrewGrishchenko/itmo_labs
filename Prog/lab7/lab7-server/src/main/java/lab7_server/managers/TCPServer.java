package lab7_server.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;

import lab7_core.models.Message;
import lab7_server.Main;
import lab7_server.commands.Command;
import lab7_server.commands.CommandManager;

public class TCPServer implements Runnable {
    private CollectionManager collectionManager;
    private CommandManager commandManager;
    
    private Reader reader;
    private Scanner scanner;

    private SelectionKey key;

    private final int port;

    private String header = "";
    private Command command;
    private String[] commandArgs = new String[]{};

    private Selector selector;

    public TCPServer (int port, CollectionManager collectionManager, CommandManager commandManager, Reader reader) {
        this.port = port;
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
        this.reader = reader;
    }

    public void acceptData () throws IOException {
        var ssc = (ServerSocketChannel) key.channel();
        var sc = ssc.accept();
        
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        System.out.println("accept with " + sc.socket().toString());
    }

    private Message inputMessage;
    private Message outputMessage;

    public void readData () throws IOException, ClassNotFoundException {
        var sc = (SocketChannel) key.channel();
        sc.configureBlocking(false);

        ByteBuffer clientData = ByteBuffer.allocate(2048);
        int numBytes = sc.read(clientData);

        if (numBytes == -1) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(clientData.array()))) {
            inputMessage = (Message) ois.readObject();
        } catch (StreamCorruptedException e) {
            key.cancel();
        };
        
        header = inputMessage.getHeader();

        switch (header) {
            case "ticket":
                command = commandManager.getCommand(commandArgs[0]);  
                command.setObj(inputMessage.getObj());
                outputMessage = new Message("response", command.run());
                break;
            case "event":
                command = commandManager.getCommand(commandArgs[0]);
                command.setObj(inputMessage.getObj());
                outputMessage = new Message("response", command.run());
                break;
            case "script":
                command = commandManager.getCommand(commandArgs[0]);
                command.setObj(inputMessage.getObj());
                outputMessage = new Message("response", command.run());
                break;
            default:
                String[] userInput = inputMessage.getCommand();
                commandArgs = userInput;
                command = commandManager.getCommand(userInput[0]);
                if (command == null) {
                    outputMessage = new Message("response", "Команда не найдена!");
                    break;
                }
                if (command.getName().equals("exit")) {
                    outputMessage = new Message("exit", command.run());
                    break;
                }
                
                command.setArgs(userInput);                                
                
                if (command.isValid() != null) {
                    outputMessage = new Message("response", command.isValid());
                } else if (command.getRequiredObject() != null) {
                    outputMessage = new Message(command.getRequiredObject());
                } else {
                    outputMessage = new Message("response", command.run());
                }
                break;
        }


        sc.register(selector, SelectionKey.OP_WRITE);
    }

    public void writeData() throws IOException {
        var sc = (SocketChannel) key.channel();
        sc.configureBlocking(false);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(outputMessage);
            ByteBuffer data = ByteBuffer.wrap(bos.toByteArray());
            ByteBuffer dataLength = ByteBuffer.allocate(32).putInt(data.limit());
            dataLength.flip();

            sc.write(dataLength);
            sc.write(data);
            oos.flush();
            oos.close();
            data.clear();
        }

        sc.register(selector, SelectionKey.OP_READ);
    }

    public void run () {
        try {
            selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress("localhost", port));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);

            Main.logger.log(Level.INFO, "Server started");

            scanner = new Scanner(reader);
            while (true) {
                if (reader.ready() && scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.equals("save")) {
                        Main.logger.log(Level.INFO, "Saving...");
                        collectionManager.saveData();
                        Main.logger.log(Level.INFO, "Saved!");
                    } else if (line.equals("exit")) {
                        Main.logger.log(Level.INFO, "Saving...");
                        collectionManager.saveData();
                        Main.logger.log(Level.INFO, "Saved!");
                        break;
                    }   
                }

                selector.select();
                
                Set<SelectionKey> keys = selector.selectedKeys();
                for (var iter = keys.iterator(); iter.hasNext(); ) {
                    key = iter.next(); iter.remove();
                    if (key.isValid()) {
                        if (key.isAcceptable()) acceptData();
                        if (key.isReadable()) readData();
                        if (key.isWritable()) writeData();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Main.logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
