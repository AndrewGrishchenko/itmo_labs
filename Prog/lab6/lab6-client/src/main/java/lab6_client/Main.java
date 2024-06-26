package lab6_client;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

import lab6_client.managers.TCPClient;

public class Main {
    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Reader reader = new InputStreamReader(System.in);
        new TCPClient("localhost", 4004, reader).run();
    }
}