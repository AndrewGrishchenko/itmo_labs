package lab6_client;

import java.util.Scanner;

import lab6_core.adapters.ScannerAdapter;
import lab6_client.managers.TCPClient;

public class Main {
    public static void main(String[] args) {
        ScannerAdapter.setScanner(new Scanner(System.in));
        new TCPClient("localhost", 4004).run();
    }
}