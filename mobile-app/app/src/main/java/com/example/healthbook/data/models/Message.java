package com.example.healthbook.data.models;

import java.io.Serializable;

public class Message implements Serializable {
    private String id;
    private String text;
    private String senderId;
    private long timestamp;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String text, String senderId, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
