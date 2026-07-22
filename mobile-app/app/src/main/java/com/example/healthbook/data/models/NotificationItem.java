package com.example.healthbook.data.models;

public class NotificationItem {
    private String id;
    private String user_id;
    private String title;
    private String body;
    private String type;
    private String created_at;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
