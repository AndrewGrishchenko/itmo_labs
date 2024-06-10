package lab7_client.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;

import lab7_client.Main;
import lab7_core.adapters.ConsoleAdapter;
import lab7_core.adapters.ScannerAdapter;
import lab7_core.exceptions.InvalidDataException;
import lab7_core.models.CommandMeta;
import lab7_core.models.CommandSchema;
import lab7_core.models.Event;
import lab7_core.models.Message;
import lab7_core.models.MessageBuilder;
import lab7_core.models.Script;
import lab7_core.models.Scripts;
import lab7_core.models.Ticket;

public class TCPClient implements Runnable {
    private SocketChannel socketChannel;

    private final String host;
    private final int port;

    private final Reader reader;
    private Scanner scanner;

    private Message msg;

    private CommandSchema commandSchema;

    public TCPClient (String host, int port, Reader reader) {
        this.host = host;
        this.port = port;
        this.reader = reader;
    }

    private String[] getUserInput() {
        String[] line = scanner.nextLine().trim().split(" ");
        if (line.length == 1 && line[0].equals("")) return null;
        return line;
    }
    
    public Scripts inspectScript (String fileName) {
        Scripts scripts = new Scripts();
        scripts.setPrimaryScript(fileName);

        try {
            String[] commands = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8).split("\n");
            scripts.addScript(new Script(fileName, commands));

            String[] line;

            for (String command : commands) {
                line = command.split(" ");
                if (line.length == 1 && line[0].length() == 0) continue;
                if (line[0].equals("execute_script")) {
                    if (!scripts.containsScript(line[1])) scripts.merge(inspectScript(line[1]));
                }
            }
            
            return scripts;
        } catch (IOException e) {
            throw new InvalidDataException("Скрипт " + fileName + " не найден!");
        }
    }

    public Message read () throws IOException {
        ByteBuffer responseData = ByteBuffer.allocate(8192);
        socketChannel.read(responseData);
        Message responseMessage = null;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(responseData.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            
            responseMessage = (Message) ois.readObject();
        } catch (ClassNotFoundException e) {
            
        } catch (StreamCorruptedException | IllegalArgumentException e) {
            Main.logger.log(Level.WARNING, "Stream corrupted. Trying again..");
            return null;
        }

        return responseMessage;
    }

    public void write (Message msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);

        ByteBuffer data = ByteBuffer.wrap(bos.toByteArray());
        socketChannel.write(data);

        oos.flush();
        oos.close();
        data.clear();
    }

    private void getSchema () throws IOException {
        ByteBuffer responseData = ByteBuffer.allocate(4096);
        socketChannel.read(responseData);

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(responseData.array());
            ObjectInputStream ois = new ObjectInputStream(bais);

            commandSchema = (CommandSchema) ois.readObject();
        } catch (ClassNotFoundException e) {

        }
    }

    public Message processInput (String[] userInput) {
        CommandMeta meta = commandSchema.getMeta(userInput[0]);
        
        if (meta == null) {
            System.out.println("Команда не найдена!");
            return null;
        }

        if (!meta.testArgC(userInput.length)) {
            System.out.println(meta.getUsage());
            return null;
        } else if (meta.getRequiredObject() != null) {
            Ticket ticket = new Ticket();
            Event event = new Event();
                            
            while (true) {
                try {
                    switch (meta.getRequiredObject()) {
                        case "ticket":
                            ticket.fillData();
                            return new MessageBuilder().command(userInput).obj(ticket).build();
                        case "event":
                            event.fillData();
                            return new MessageBuilder().command(userInput).obj(event).build();
                        case "script":
                            return new MessageBuilder().command(userInput).obj(inspectScript(userInput[1])).build();
                        default:
                            return null;
                    }
                } catch (InvalidDataException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
        } else {
            return new MessageBuilder().command(userInput).build();
        }
    }

    private void getResponse () throws IOException {
        Message response = null;
        while (true) {
            write(msg);
            response = read();
            if (response != null) break;
        }

        System.out.println(response.getResponse());
    }

    public void run () {
        boolean isRunning = true;
        msg = null;

        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            getSchema();
        } catch (IOException e) {
            Main.logger.log(Level.INFO, "Server is unavailable. Try again later");
            return;
        }

        Main.logger.log(Level.INFO, "Connected to " + host + ":" + port);
        
        scanner = new Scanner(reader);
        ScannerAdapter.setInteractiveScanner(scanner);

        while (isRunning) {
            try {
                while (true) {
                    ConsoleAdapter.prompt();
                    String[] userInput = new String[]{};
                    while (true) {
                        if (reader.ready()) {
                            userInput = getUserInput();
                            break;
                        }
                    }

                    if (userInput == null) continue;

                    msg = processInput(userInput);
                    if (msg == null) continue;

                    getResponse();
                }
            } catch (IOException | BufferUnderflowException e) {
                try {
                    e.printStackTrace();
                    socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                    Main.logger.log(Level.INFO, "Reconnected to " + host + ":" + port);
                    getSchema();
                    getResponse();
                } catch (IOException exc) {
                    isRunning = false;
                }
            }
        }

        Main.logger.log(Level.INFO, "Connection closed");
    }
}