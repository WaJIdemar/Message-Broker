package ru.message.server.processors;

import ru.message.common.BrokerMessage;
import ru.message.common.ServerMessage;
import ru.message.common.ServerRequest;
import ru.message.server.database.DataProvider;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static ru.message.common.ServerRequest.REPLY;

public class SimpleConnectionProcessor extends AbstractConnectionProcessor {

    private final DataProvider dataProvider;

    public SimpleConnectionProcessor(Socket socket, DataProvider dataProvider) {
        super(socket);
        this.dataProvider = dataProvider;
    }

    @Override
    protected void processConnection() throws Exception {
        ServerMessage serverMessage = getServerMessage();
        Map<String, String> params = serverMessage.getParams();
        ServerRequest serverRequest = serverMessage.getServerRequest();
        switch (serverRequest) {
            case CONNECT_OR_REGISTER:
                connectOrRegisterConsumer(params);
                break;

            case GET_MESSAGE:
                getMessage(params);
                break;

            case COMMIT_OFFSET:
                commitOffset(params);
                break;

            case PUT_MESSAGE:
                putMessage(serverMessage.getBrokerMessage());
                break;
        }
    }

    protected void connectOrRegisterConsumer(Map<String, String> params) throws Exception {
        int consumerOffset = dataProvider.connectOrRegisterConsumer(params.get("consumer_name"), params.get("consumer_group"));
        ServerMessage serverMessage = new ServerMessage(REPLY);
        Map<String, String> replyParams = new HashMap<>();
        replyParams.put("consumer_offset", String.valueOf(consumerOffset));
        serverMessage.setParams(replyParams);
        sendServerMessage(serverMessage);
    }

    protected void getMessage(Map<String, String> params) throws Exception {
        BrokerMessage brokerMessage = dataProvider.getMessage(params.get("consumer_group"), Integer.parseInt(params.get("consumer_offset")));
        ServerMessage serverMessage = new ServerMessage(REPLY);
        serverMessage.setBrokerMessage(brokerMessage);
        sendServerMessage(serverMessage);
    }

    protected void commitOffset(Map<String, String> params) throws Exception {
        dataProvider.commitConsumerOffset(params.get("consumer_name"), Integer.parseInt(params.get("consumer_offset")));
        int consumerOffset = dataProvider.getConsumerOffset(params.get("consumer_name"));
        ServerMessage serverMessage = new ServerMessage(REPLY);
        Map<String, String> replyParams = new HashMap<>();
        replyParams.put("consumer_offset", String.valueOf(consumerOffset));
        serverMessage.setParams(replyParams);
        sendServerMessage(serverMessage);
    }

    protected void putMessage(BrokerMessage message) throws Exception {
        dataProvider.insertMessage(message);
    }
}
