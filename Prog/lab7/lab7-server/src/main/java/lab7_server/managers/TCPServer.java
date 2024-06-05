package lab7_server.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import lab7_core.models.Message;
import lab7_core.models.MessageBuilder;
import lab7_server.Main;
import lab7_server.commands.Command;
import lab7_server.commands.CommandManager;
import lab7_server.models.ClientData;

public class TCPServer implements Runnable {
    private CollectionManager collectionManager;
    private CommandManager commandManager;
    
    private Reader reader;
    private Scanner scanner;

    private SelectionKey key;

    private final int port;

    private Command command;
    private String[] commandArgs = new String[]{};

    private Selector selector;
    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private ReentrantLock lock = new ReentrantLock();

    private HashMap<SelectionKey, ClientData> clientMap = new HashMap<>();

    public TCPServer (int port, CollectionManager collectionManager, CommandManager commandManager, Reader reader) {
        this.port = port;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
        this.reader = reader;
    }

    public void acceptData (SelectionKey key) throws IOException {
        var ssc = (ServerSocketChannel) key.channel();
        var sc = ssc.accept();
        
        sendSchema(sc);
        
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
    }

    private void sendSchema (SocketChannel sc) throws IOException {
        sc.configureBlocking(false);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(commandManager.genSchema());
        ByteBuffer data = ByteBuffer.wrap(bos.toByteArray());

        sc.write(data);
        oos.flush();
        oos.close();
        data.clear();
    }

    private void setMessage (SelectionKey key, Message message) {
        ClientData data = (ClientData) clientMap.get(key);
        data.setMessage(message);
        clientMap.replace(key, data);
    }

    private void clearKey (SelectionKey key) {
        if (key.isValid()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                socketChannel.close();
            } catch (IOException e) {

            }
            key.attach(null);
            key.cancel();
        }
    }

    private void proccessMessage (Message message, AuthManager authManager) {
        commandArgs = message.getCommand();

        command = commandManager.getCommand(commandArgs[0]);
        command.setObj(message.getObject());
        command.setLock(lock);
        command.setAuthManager(authManager);
        command.setArgs(commandArgs);

        // Callable<String> callable = new Callable<String>() {
        //     public String call() {
        //         return command.compute();
        //     }
        // };

        // ForkJoinTask<String> task = ForkJoinTask.adapt(callable);

        // String some = forkJoinPool.invoke(task);
        // setMessage(key, new MessageBuilder().response(some).build());
        setMessage(key, new MessageBuilder().response(command.compute()).build());
    }

    public void readData (SelectionKey key) throws IOException, ClassNotFoundException {
        var sc = (SocketChannel) key.channel();
        sc.configureBlocking(false);

        ByteBuffer clientData = ByteBuffer.allocate(4096);
        sc.read(clientData);

        new Thread(() -> {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(clientData.array());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message inputMessage = (Message) ois.readObject();
                
                AuthManager authManager = clientMap.get(key).getAuthManager();

                forkJoinPool.submit(() -> {
                    proccessMessage(inputMessage, authManager);
                }).join();

                sc.register(selector, SelectionKey.OP_WRITE);
            } catch (IOException | ClassNotFoundException e) {
                clearKey(key);
            }
        }).start();

        writeData(key);
    }

    private void writeData (SelectionKey key) throws IOException {
        executorService.submit(() -> {
            try {
                if (!key.isValid()) return;
                while(clientMap.get(key).getMessage() == null);

                var sc = (SocketChannel) key.channel();
                sc.configureBlocking(false);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                
                while (clientMap.get(key).getMessage() == null);
                oos.writeObject(clientMap.get(key).getMessage());
                ByteBuffer data = ByteBuffer.wrap(bos.toByteArray());

                sc.write(data);
                oos.flush();
                oos.close();
                data.clear();

                setMessage(key, null);
                sc.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                clearKey(key);
            }
        });
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
                        if (!clientMap.containsKey(key)) {
                            clientMap.put(key, new ClientData());
                        }
                
                        try {
                            if (key.isAcceptable()) acceptData(key);
                            if (key.isReadable()) readData(key);
                        } catch (IOException | ClassNotFoundException e) {
                            clearKey(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Main.logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
