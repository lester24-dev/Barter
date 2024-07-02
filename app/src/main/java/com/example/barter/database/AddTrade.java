package com.example.barter.database;

public class AddTrade {
    String currentDate ,currentTime,product_name,product_price,total_price,total_quantity,user,product_image;

    public AddTrade(){

    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

   // public String getTotal_price() {
        //return total_price;
  //  }

  //  public void setTotal_price(String total_price) {
     //   this.total_price = total_price;
  //  }

 //   public String getTotal_quantity() {
  //      return total_quantity;
  //  }

 //   public void setTotal_quantity(String total_quantity) {
     //   this.total_quantity = total_quantity;
  //  }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }
}
