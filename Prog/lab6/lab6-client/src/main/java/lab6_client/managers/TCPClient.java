package lab6_client.managers;

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

import lab6_client.Main;
import lab6_core.adapters.ConsoleAdapter;
import lab6_core.adapters.ScannerAdapter;
import lab6_core.exceptions.InvalidDataException;
import lab6_core.models.Event;
import lab6_core.models.Message;
import lab6_core.models.Script;
import lab6_core.models.Scripts;
import lab6_core.models.Ticket;

public class TCPClient implements Runnable {
    private SocketChannel clientSocket;

    private final String host;
    private final int port;

    private final Reader reader;
    private Scanner scanner;

    private String header;

    private Object model;

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
    
    public Scripts inspectScript (String fileName) throws IOException {
        Scripts scripts = new Scripts();

        String[] commands = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8).split("\n");
        scripts.addScript(new Script(fileName, commands));

        String[] line;

        for (String command : commands) {
            line = command.split(" ");
            if (line[0].equals("execute_script")) {
                if (!scripts.containsScript(line[1])) scripts.merge(inspectScript(line[1]));
            }
        }
        
        return scripts;
    }

    public Message read () throws IOException {
        ByteBuffer responseLengthData = ByteBuffer.allocate(32);
        clientSocket.read(responseLengthData);
        responseLengthData.flip();
        int responseLength = responseLengthData.getInt();

        ByteBuffer responseData = ByteBuffer.allocate(responseLength);
        clientSocket.read(responseData);
        
        Message responseMessage = null;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(responseData.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            
            responseMessage = (Message) ois.readObject();
        } catch (ClassNotFoundException e) {
            
        } catch (StreamCorruptedException e) {
            Main.logger.log(Level.WARNING, "Stream corrupted. Trying again..");
            return null;
        }

        return responseMessage;
    }

    public void write (Message msg) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(msg);

            ByteBuffer data = ByteBuffer.wrap(bos.toByteArray());

            clientSocket.write(data);
            oos.flush();
            oos.close();
        }
    }

    public void run () {
        try {
            Thread.sleep(1000);
            
            // clientSocket = new Socket(host, port);
            try {
                clientSocket = SocketChannel.open(new InetSocketAddress(host, port));
            } catch (IOException e) {
                Main.logger.log(Level.INFO, "Server is unavailable. Try again later");
                return;
            }

            Main.logger.log(Level.INFO, "Connected to " + host + ":" + port);
            
            scanner = new Scanner(reader);
            ScannerAdapter.setInteractiveScanner(scanner);

            Message msg;
            String fileName = "";
            header = "";

            while (true) {
                switch (header) {
                    case "ticket":
                        if (model == null) model = new Ticket();
                        try {
                            ((Ticket) model).fillData();
                        } catch (InvalidDataException e) {
                            Main.logger.log(Level.SEVERE, e.getMessage());
                            continue;
                        }
                        
                        msg = new Message("ticket", model);
                        break;
                    case "event":
                        if (model == null) model = new Event();
                        try {
                            ((Event) model).fillData();
                        } catch (InvalidDataException e) {
                            Main.logger.log(Level.SEVERE, e.getMessage());
                            continue;
                        }
                        
                        msg = new Message("event", model);
                        break;
                    case "script":
                        try {
                            Scripts scripts = inspectScript(fileName);
                            scripts.setPrimaryScript(fileName);
                            msg = new Message("script", fileName, scripts);
                        } catch (IOException e) {
                            Main.logger.log(Level.SEVERE, "Файл " + e.getMessage() + " не найден!");
                            header = "";
                            continue;
                        }
                        break;
                    default:
                        ConsoleAdapter.prompt();
                        String[] userInput = new String[]{};
                        while (true) {
                            if (reader.ready()) {
                                userInput = getUserInput();
                                break;
                            }
                            //TODO: idk
                            // } else if (clientSocket.getInputStream().available() > 0) {
                            //     if (((Message) in.readObject()).getHeader().equals("shutdown")) {
                            //         return;
                            //     }
                            // }
                        }

                        if (userInput == null) continue;

                        if (userInput[0].equals("execute_script") && userInput.length == 2) {
                            fileName = userInput[1];
                            
                        }

                        msg = new Message("command", userInput);
                        break;
                }

                Message response = null;
                while (true) {
                    write(msg);
                    response = read();
                    if (response != null) break;
                }

                header = response.getHeader();
                
                if (header.equals("response")) {
                    System.out.println(response.getResponse());
                } else if (header.equals("exit")) {
                    System.out.println(response.getResponse());
                    break;
                }
            }
                
                
            // }

            /*in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            Message msg;
            scanner = new Scanner(reader);
            ScannerAdapter.setInteractiveScanner(scanner);

            String fileName = "";
            header = "";

            while (true) {
                switch (header) {
                    case "ticket":
                        if (model == null) model = new Ticket();
                        try {
                            ((Ticket) model).fillData();
                        } catch (InvalidDataException e) {
                            Main.logger.log(Level.SEVERE, e.getMessage());
                            continue;
                        }
                        
                        msg = new Message("ticket", model);
                        break;
                    case "event":
                        if (model == null) model = new Event();
                        try {
                            ((Event) model).fillData();
                        } catch (InvalidDataException e) {
                            Main.logger.log(Level.SEVERE, e.getMessage());
                            continue;
                        }
                        
                        msg = new Message("event", model);
                        break;
                    case "script":
                        try {
                            Scripts scripts = inspectScript(fileName);
                            scripts.setPrimaryScript(fileName);
                            msg = new Message("script", fileName, scripts);
                        } catch (IOException e) {
                            Main.logger.log(Level.SEVERE, "Файл " + e.getMessage() + " не найден!");
                            header = "";
                            continue;
                        }
                        break;
                    default:
                        ConsoleAdapter.prompt();
                        String[] userInput = new String[]{};
                        while (true) {
                            if (reader.ready()) {
                                userInput = getUserInput();
                                break;
                            } else if (clientSocket.getInputStream().available() > 0) {
                                if (((Message) in.readObject()).getHeader().equals("shutdown")) {
                                    return;
                                }
                            }
                        }

                        if (userInput == null) continue;

                        if (userInput[0].equals("execute_script") && userInput.length == 2) {
                            fileName = userInput[1];
                            
                        }

                        msg = new Message("command", userInput);
                        break;
                }
                
                out.writeObject(msg);
                out.flush();

                Message response = (Message) in.readObject();
                header = response.getHeader();
                
                if (header.equals("response")) {
                    System.out.println(response.getResponse());
                } else if (header.equals("exit")) {
                    System.out.println(response.getResponse());
                    break;
                }
            }*/
        } catch (IOException | InterruptedException | BufferUnderflowException e) { //| ClassNotFoundException e) {
            
        } finally {
            Main.logger.log(Level.INFO, "Connection closed");
        }
    }
}
