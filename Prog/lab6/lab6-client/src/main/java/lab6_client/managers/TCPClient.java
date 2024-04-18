package lab6_client.managers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import lab6_core.adapters.ConsoleAdapter;
import lab6_core.adapters.ScannerAdapter;
import lab6_core.models.Event;
import lab6_core.models.Message;
import lab6_core.models.Script;
import lab6_core.models.Scripts;
import lab6_core.models.Ticket;

public class TCPClient implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String host;
    private final int port;

    private final Reader reader;
    private Scanner scanner;

    private String header;

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

    public void run () {
        try {
            Thread.sleep(1000);
            
            clientSocket = new Socket(host, port);
            
            System.out.println("client started");
            
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            Message msg;
            scanner = new Scanner(reader);
            ScannerAdapter.setInteractiveScanner(scanner);

            String fileName = "";
            header = "";

            while (true) {
                switch (header) {
                    case "ticket":
                        Ticket ticket = new Ticket();
                        ticket.fillData();
                        msg = new Message("ticket", ticket);
                        break;
                    case "event":
                        Event event = new Event();
                        event.fillData();
                        msg = new Message("event", event);
                        break;
                    case "script":
                        try {
                            Scripts scripts = inspectScript(fileName);
                            scripts.setPrimaryScript(fileName);
                            msg = new Message("script", fileName, scripts);
                        } catch (IOException e) {
                            ConsoleAdapter.printErr("Файл " + e.getMessage() + " не найден!");
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
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.out.println("server closed connection");
        }
    }
}
