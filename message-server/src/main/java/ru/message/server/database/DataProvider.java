package ru.message.server.database;

import ru.message.common.BrokerMessage;

public interface DataProvider {

    void insertMessage(BrokerMessage brokerMessage) throws Exception;
    BrokerMessage getMessage(String group, int offset) throws Exception;
    int connectOrRegisterConsumer(String consumerName, String consumerGroup) throws Exception;
    void commitConsumerOffset(String consumerName, int consumerOffset) throws Exception;
    int getConsumerOffset(String consumerName) throws Exception;
}
