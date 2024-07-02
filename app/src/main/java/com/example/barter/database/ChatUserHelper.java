package com.example.barter.database;

public class ChatUserHelper {

    public String reciever, sender, message, timestamp, reciever_name, reciever_img, status, type;


    public ChatUserHelper(){

    }
    public ChatUserHelper(String reciever, String sender, String message, String timestamp, String reciever_name, String reciever_img, String status, String type){
        this.reciever = reciever;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.reciever_name = reciever_name;
        this.reciever_img = reciever_img;
        this.status = status;
        this.type = type;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReciever_name() {
        return reciever_name;
    }

    public void setReciever_name(String reciever_name) {
        this.reciever_name = reciever_name;
    }

    public String getReciever_img() {
        return reciever_img;
    }

    public void setReciever_img(String reciever_img) {
        this.reciever_img = reciever_img;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
