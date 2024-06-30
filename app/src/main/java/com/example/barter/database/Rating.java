package com.example.barter.database;

import java.util.Date;

public class Rating {

    private String name;
    private String review;
    private String timeStamp;
    private String totalStarGiven;
    private String userID;
    private String product_seller;

    public Rating(String name,String review,String timeStamp,String totalStarGiven,String userID,String product_seller){
        this.name = name;
        this.review = review;
        this.timeStamp = timeStamp;
        this.totalStarGiven = totalStarGiven;
        this.userID = userID;
        this.product_seller = product_seller;
    }

    public Rating(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTotalStarGiven() {
        return totalStarGiven;
    }

    public void setTotalStarGiven(String totalStarGiven) {
        this.totalStarGiven = totalStarGiven;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProduct_seller() {
        return product_seller;
    }

    public void setProduct_seller(String product_seller) {
        this.product_seller = product_seller;
    }
}
