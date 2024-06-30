package com.example.barter.database;

public class TradeReq {

    String id, userid, product_name_user, product_price_user, product_image_user,
            sellerid, product_name_seller, product_price_seller, product_image_seller, status, timestamp, payment, orderType, user_address, user_name;

    public TradeReq(String id, String userid, String product_name_user, String product_price_user, String product_image_user, String sellerid,
                    String product_name_seller, String product_price_seller, String product_image_seller, String status, String timestamp,
                    String payment, String orderType, String user_address, String user_name){
        this.id = id;
        this.userid = userid;
        this.product_name_user = product_name_user;
        this.product_price_user = product_price_user;
        this.product_image_user = product_image_user;
        this.sellerid = sellerid;
        this.product_name_seller = product_name_seller;
        this.product_price_seller = product_price_seller;
        this.product_image_seller = product_image_seller;
        this.status = status;
        this.timestamp = timestamp;
        this.payment  = payment;
        this.orderType = orderType;
        this.user_name = user_name;
        this.user_address = user_address;

    }

    public TradeReq(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProduct_name_user() {
        return product_name_user;
    }

    public void setProduct_name_user(String product_name_user) {
        this.product_name_user = product_name_user;
    }

    public String getProduct_price_user() {
        return product_price_user;
    }

    public void setProduct_price_user(String product_price_user) {
        this.product_price_user = product_price_user;
    }

    public String getProduct_image_user() {
        return product_image_user;
    }

    public void setProduct_image_user(String product_image_user) {
        this.product_image_user = product_image_user;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public String getProduct_name_seller() {
        return product_name_seller;
    }

    public void setProduct_name_seller(String product_name_seller) {
        this.product_name_seller = product_name_seller;
    }

    public String getProduct_price_seller() {
        return product_price_seller;
    }

    public void setProduct_price_seller(String product_price_seller) {
        this.product_price_seller = product_price_seller;
    }

    public String getProduct_image_seller() {
        return product_image_seller;
    }

    public void setProduct_image_seller(String product_image_seller) {
        this.product_image_seller = product_image_seller;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPayment() {
        return payment;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
