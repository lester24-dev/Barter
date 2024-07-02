package com.example.barter.database;

public class Users {
    String name, username, email, phoneNo, password, filepath, address, age, id, status, govt_id, date_register, admin_approved;

    public Users(){

    }

    public Users(String name, String username, String email, String phoneNo, String password, String filepath, String address, String age, String id, String status, String govt_id, String date_register, String admin_approved) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.filepath = filepath;
        this.address = address;
        this.age = age;
        this.id = id;
        this.status = status;
        this.govt_id = govt_id;
        this.date_register = date_register;
        this.admin_approved = admin_approved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGovt_id() {
        return govt_id;
    }

    public void setGovt_id(String govt_id) {
        this.govt_id = govt_id;
    }

    public String getDate_register() {
        return date_register;
    }

    public void setDate_register(String date_register) {
        this.date_register = date_register;
    }

    public String getAdmin_approved() {
        return admin_approved;
    }

    public void setAdmin_approved(String admin_approved) {
        this.admin_approved = admin_approved;
    }
}
