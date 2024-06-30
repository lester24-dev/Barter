package com.example.barter.database;

public class NotifHelperClass {
    String title, receiver_name, sender, reciever, message, type, status;

    public NotifHelperClass(String title, String receiver_name, String sender, String reciver, String message, String type, String status){
            this.title = title;
            this.receiver_name = receiver_name;
            this.sender = sender;
            this.reciever = reciver;
            this.message = message;
            this.type = type;
            this.status = status;
    }

    public NotifHelperClass(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
