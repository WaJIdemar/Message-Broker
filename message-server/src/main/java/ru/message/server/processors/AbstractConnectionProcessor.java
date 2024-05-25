package ru.message.server.processors;

import ru.message.common.AbstractSocketInteraction;
import java.net.Socket;

public abstract class AbstractConnectionProcessor extends AbstractSocketInteraction implements Runnable {
    public AbstractConnectionProcessor(Socket socket) {
        super(socket);
    }

    abstract protected void processConnection() throws Exception;

    public void run() {
        try {
            while (socket.isConnected()) {
                processConnection();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            close();
        } finally {
            close();
        }
    }
}
