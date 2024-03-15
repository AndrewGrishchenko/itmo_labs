package lab6_client.managers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lab6_core.adapters.ConsoleAdapter;
import lab6_core.adapters.ScannerAdapter;
import lab6_core.models.Event;
import lab6_core.models.Message;
import lab6_core.models.Ticket;

public class TCPClient implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String host;
    private final int port;

    public TCPClient (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run () {
        try {
            try {
                Thread.sleep(1000);
                
                clientSocket = new Socket(host, port);
                
                System.out.println("client started");
                
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                Message msg;

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
                            String[] userInput = ScannerAdapter.getUserInput();
                            if (userInput == null) continue;

                            msg = new Message("command", userInput);
                            break;
                    }
                    
                    out.writeObject(msg);
                    out.flush();

                    //not blocking
                    Message response = (Message) in.readObject();
                    header = response.getHeader();
                    if (header.equals("response")) {
                        System.out.println(response.getResponse());
                    } else if (header.equals("exit")) {
                        System.out.println(response.getResponse());
                        break;
                    }
                }
            } finally {
                System.out.println("disconnected");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
