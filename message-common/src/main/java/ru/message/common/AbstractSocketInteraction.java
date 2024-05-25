package ru.message.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class AbstractSocketInteraction {

    protected final Socket socket;

    public AbstractSocketInteraction(Socket socket) {
        this.socket = socket;
    }

    protected ServerMessage getServerMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        return (ServerMessage) input.readObject();
    }

    protected void sendServerMessage(ServerMessage serverMessage) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(serverMessage);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
