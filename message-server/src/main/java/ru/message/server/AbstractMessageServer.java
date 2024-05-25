package ru.message.server;

import ru.message.server.processors.AbstractConnectionProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMessageServer extends Thread {

    private final int port;
    private final int threadPoolCount;

    public AbstractMessageServer(int port, int threadPoolCount) {
        this.port = port;
        this.threadPoolCount = threadPoolCount;
    }

    public void run() {
        try {
            runServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService service = Executors.newFixedThreadPool(threadPoolCount);
        try (service) {
            try (serverSocket) {
                System.out.println("Start Server" + serverSocket);
                while (true) {
                    Socket socket = serverSocket.accept();
                    try {
                        System.out.println("Connection accepted:" + socket);
                        service.execute(getMessageProcessor(socket));
                    } catch (Exception e){
                        System.out.println("Error closing...");
                        socket.close();
                    }
                }
            }
        }
    }

    abstract protected <T extends AbstractConnectionProcessor> T getMessageProcessor(Socket socket);
}
