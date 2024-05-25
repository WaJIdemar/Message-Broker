package ru.message.common;

import java.io.Serializable;

public class BrokerMessage implements Serializable {
    private final String key;
    private final String text;

    public BrokerMessage(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }
}
