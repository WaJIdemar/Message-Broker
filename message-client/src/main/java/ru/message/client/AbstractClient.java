package ru.message.client;

import ru.message.common.AbstractSocketInteraction;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractClient extends AbstractSocketInteraction {

    public AbstractClient(String messageBrokerHost, int messageBrokerPort) throws IOException {
        super(new Socket(messageBrokerHost, messageBrokerPort));
    }
}
