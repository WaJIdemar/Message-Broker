package ru.message.client;

import ru.message.common.BrokerMessage;
import ru.message.common.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ru.message.common.ServerRequest.*;

public class PullConsumer extends AbstractClient {

    private final String consumerName;
    private final String consumerGroup;
    private int consumerOffset;

    public PullConsumer(String messageBrokerHost, int messageBrokerPort, String consumerName, String consumerGroup)
            throws IOException, ClassNotFoundException {
        super(messageBrokerHost, messageBrokerPort);
        this.consumerName = consumerName;
        this.consumerGroup = consumerGroup;
        connectOrRegisterConsumer();
    }

    private void connectOrRegisterConsumer() throws IOException, ClassNotFoundException {
        ServerMessage serverMessage = new ServerMessage(CONNECT_OR_REGISTER);
        Map<String, String> params = new HashMap<>();
        params.put("consumer_name", consumerName);
        params.put("consumer_group", consumerGroup);
        serverMessage.setParams(params);
        sendServerMessage(serverMessage);
        ServerMessage serverReply = getServerMessage();
        consumerOffset = Integer.parseInt(serverReply.getParams().get("consumer_offset"));
    }

    public BrokerMessage getMessage() throws IOException, ClassNotFoundException {
        ServerMessage serverMessage = new ServerMessage(GET_MESSAGE);
        Map<String, String> params = new HashMap<>();
        params.put("consumer_group", consumerGroup);
        params.put("consumer_offset", String.valueOf(consumerOffset));
        serverMessage.setParams(params);
        sendServerMessage(serverMessage);
        ServerMessage serverReply = getServerMessage();
        return serverReply.getBrokerMessage();
    }

    public void commitOffset(int offset) throws IOException, ClassNotFoundException {
        ServerMessage serverMessage = new ServerMessage(COMMIT_OFFSET);
        Map<String, String> params = new HashMap<>();
        params.put("consumer_name", consumerName);
        params.put("consumer_offset", String.valueOf(offset));
        serverMessage.setParams(params);
        sendServerMessage(serverMessage);
        ServerMessage serverReply = getServerMessage();
        consumerOffset = Integer.parseInt(serverReply.getParams().get("consumer_offset"));
    }
}
