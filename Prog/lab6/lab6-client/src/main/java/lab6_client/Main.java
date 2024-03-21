package lab6_client;

import java.io.InputStreamReader;
import java.io.Reader;

import lab6_client.managers.TCPClient;

public class Main {
    public static void main(String[] args) {
        Reader reader = new InputStreamReader(System.in);
        new TCPClient("localhost", 4004, reader).run();
    }
}