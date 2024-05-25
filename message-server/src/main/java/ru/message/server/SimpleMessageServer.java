package ru.message.server;

import ru.message.server.database.DataProvider;
import ru.message.server.processors.SimpleConnectionProcessor;

import java.net.Socket;

public class SimpleMessageServer extends AbstractMessageServer {
    private final DataProvider dataProvider;

    public SimpleMessageServer(int serverPort, int threadPoolCount, DataProvider dataProvider) {
        super(serverPort, threadPoolCount);
        this.dataProvider = dataProvider;
    }


    @Override
    @SuppressWarnings("unchecked")
    protected SimpleConnectionProcessor getMessageProcessor(Socket socket) {
        return new SimpleConnectionProcessor(socket, dataProvider);
    }
}
