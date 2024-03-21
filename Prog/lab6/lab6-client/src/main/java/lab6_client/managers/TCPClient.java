package lab6_client.managers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.Scanner;

import lab6_core.adapters.ConsoleAdapter;
import lab6_core.models.Event;
import lab6_core.models.Message;
import lab6_core.models.Ticket;

public class TCPClient implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String host;
    private final int port;

    private final Reader reader;
    private Scanner scanner;

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

    public void run () {
        try {
            Thread.sleep(1000);
            
            clientSocket = new Socket(host, port);
            
            System.out.println("client started");
            
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            Message msg;
            scanner = new Scanner(reader);

            String header = "";

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
                    default:
                        ConsoleAdapter.prompt();
                        String[] userInput = new String[]{};
                        while (true) {
                            if (reader.ready()) {
                                userInput = getUserInput();
                                break;
                            } else if (clientSocket.getInputStream().available() > 0) {
                                if (((Message) in.readObject()).getHeader().equals("shutdown")) {
                                    System.out.println("server closed connection");
                                    return;
                                }
                            }
                        }

                        if (userInput == null) continue;

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
            
        } finally {
            System.out.println("server closed connection");
        }
    }
}
