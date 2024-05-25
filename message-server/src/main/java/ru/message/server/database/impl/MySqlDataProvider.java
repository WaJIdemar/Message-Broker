package ru.message.server.database.impl;

import ru.message.common.BrokerMessage;
import ru.message.server.database.DataProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.message.server.database.impl.MySqlMessageConstant.*;
import static ru.message.server.database.impl.MySqlConsumerConstant.*;

public class MySqlDataProvider implements DataProvider {

    private final String SQL_INSERT_MESSAGE = "INSERT INTO messages (" + MESSAGE_KEY + ", " + MESSAGE_TEXT + ") "
            + "VALUES ('%s', '%s')";
    private final String SQL_GET_MESSAGE = "SELECT * FROM messages WHERE " + MESSAGE_KEY + " IN (%s) LIMIT 1 OFFSET %d";
    private final String SQL_INSERT_CONSUMER = "INSERT INTO consumers (" + CONSUMER_NAME + ", " + CONSUMER_GROUP + ", "
            + CONSUMER_OFFSET + ") VALUES ('%s', '%s', 0)";
    private final String SQL_GET_MESSAGE_KEYS_ON_GROUP = "SELECT " + MESSAGE_KEY + " FROM group_key WHERE "
            + CONSUMER_GROUP + " = '%s'";
    private final String SQL_SET_CONSUMER_OFFSET = "UPDATE consumers SET " + CONSUMER_OFFSET + " = %d WHERE " + CONSUMER_NAME + " = '%s'";
    private final String SQL_GET_CONSUMER_OFFSET = "SELECT " + CONSUMER_OFFSET + " FROM consumers WHERE " + CONSUMER_NAME + " = '%s'";
    private final String SQL_CHECK_CONSUMER_REGISTERED = "SELECT COUNT(*) as count FROM consumers WHERE " + CONSUMER_NAME + " = '%s' AND "
            + CONSUMER_GROUP + " = '%s'";

    private final Connection connection;

    public MySqlDataProvider(String url, String userName, String password) throws SQLException {
        connection = DriverManager.getConnection(url, userName, password);
    }

    @Override
    public void insertMessage(BrokerMessage brokerMessage) throws SQLException {
        connection.createStatement().execute(String.format(SQL_INSERT_MESSAGE, brokerMessage.getKey(), brokerMessage.getText()));
    }

    @Override
    public BrokerMessage getMessage(String consumerGroup, int offset) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery(String.format(SQL_GET_MESSAGE, getMessageKeys(consumerGroup), offset));
        return createMessage(resultSet);
    }

    @Override
    public int connectOrRegisterConsumer(String consumerName, String consumerGroup) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery(String.format(SQL_CHECK_CONSUMER_REGISTERED, consumerName, consumerGroup));
        resultSet.next();
        int consumerOffset = 0;
        if (resultSet.getInt("count") > 0) {
            consumerOffset = getConsumerOffset(consumerName);
        } else {
            connection.createStatement().execute(String.format(SQL_INSERT_CONSUMER, consumerName, consumerGroup));
        }
        return consumerOffset;
    }

    @Override
    public void commitConsumerOffset(String consumerName, int consumerOffset) throws SQLException {
        connection.createStatement().execute(String.format(SQL_SET_CONSUMER_OFFSET, consumerOffset, consumerName));
    }

    @Override
    public int getConsumerOffset(String consumerName) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery(String.format(SQL_GET_CONSUMER_OFFSET, consumerName));
        resultSet.next();
        return resultSet.getInt(CONSUMER_OFFSET);
    }

    private BrokerMessage createMessage(ResultSet resultSet) throws SQLException {
        if (!resultSet.isBeforeFirst()) {
            return null;
        }
        resultSet.next();
        return new BrokerMessage(resultSet.getString(MESSAGE_KEY), resultSet.getString(MESSAGE_TEXT));
    }

    private String getMessageKeys(String consumerGroup) throws SQLException {
        String sql = String.format(SQL_GET_MESSAGE_KEYS_ON_GROUP, consumerGroup);
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        List<String> messageKeys = new ArrayList<>();
        while (resultSet.next()) {
            messageKeys.add("'" + resultSet.getString(MESSAGE_KEY) + "'");
        }

        return String.join(", ", messageKeys);
    }
}
