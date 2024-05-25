package ru.message.common;

import java.io.Serializable;
import java.util.Map;

public class ServerMessage implements Serializable {

    private BrokerMessage brokerMessage;
    private Map<String, String> params;
    private final ServerRequest serverRequest;

    public ServerMessage(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }

    public void setBrokerMessage(BrokerMessage brokerMessage) {
        this.brokerMessage = brokerMessage;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public BrokerMessage getBrokerMessage() {
        return brokerMessage;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public ServerRequest getServerRequest() {
        return serverRequest;
    }
}
