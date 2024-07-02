package com.example.barter.database;

public class ReturnHelper {
    private String name;
    private String returns;
    private String userID;
    private String product_seller;
    private String productId;


    public ReturnHelper(String name, String returns, String userID, String product_seller, String productId){
        this.name = name;
        this.returns = returns;
        this.userID = userID;
        this.product_seller = product_seller;
        this.productId = productId;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturns() {
        return returns;
    }

    public void setReturns(String returns) {
        this.returns = returns;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
