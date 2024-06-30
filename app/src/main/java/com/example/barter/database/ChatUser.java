package com.example.barter.database;

public class ChatUser {

    String name, filepath, id;

    public ChatUser() {
    }

    public ChatUser(String name, String filepath, String id) {
        this.name = name;
        this.filepath = filepath;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
