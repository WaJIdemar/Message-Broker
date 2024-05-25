package ru.message.client;

import ru.message.common.BrokerMessage;
import ru.message.common.ServerMessage;

import java.io.*;

import static ru.message.common.ServerRequest.PUT_MESSAGE;

public class Producer extends AbstractClient {

    public Producer(String messageBrokerHost, int messageBrokerPort) throws IOException {
        super(messageBrokerHost, messageBrokerPort);
    }

    public void putMessage(BrokerMessage brokerMessage) throws IOException {
        ServerMessage serverMessage = new ServerMessage(PUT_MESSAGE);
        serverMessage.setBrokerMessage(brokerMessage);
        sendServerMessage(serverMessage);
    }
}
