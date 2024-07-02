package com.example.barter.database;

public class ProductHelperClass {
    String product_name, product_category, product_color, product_des, product_image, product_brand, product_size, product_year, product_seller, product_price, date, product_address, hash;

    public ProductHelperClass(){

    }

    public ProductHelperClass(String product_name, String product_category, String product_color, String product_des, String product_image, String product_brand, String product_size, String product_year, String product_price, String product_seller,String date,String product_address, String hash){
     this.product_name = product_name;
     this.product_category = product_category;
     this.product_color = product_color;
     this.product_des = product_des;
     this.product_image = product_image;
     this.product_brand = product_brand;
     this.product_size = product_size;
     this.product_year = product_year;
     this.product_seller = product_seller;
     this.product_price = product_price;
     this.date = date;
     this.product_address = product_address;
     this.hash = hash;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_color() {
        return product_color;
    }

    public void setProduct_color(String product_color) {
        this.product_color = product_color;
    }

    public String getProduct_des() {
        return product_des;
    }

    public void setProduct_des(String product_des) {
        this.product_des = product_des;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_brand() {
        return product_brand;
    }

    public void setProduct_brand(String product_brand) {
        this.product_brand = product_brand;
    }

    public String getProduct_size() {
        return product_size;
    }

    public void setProduct_size(String product_size) {
        this.product_size = product_size;
    }

    public String getProduct_year() {
        return product_year;
    }

    public void setProduct_year(String product_year) {
        this.product_year = product_year;
    }

    public String getProduct_seller() {
        return product_seller;
    }

    public void setProduct_seller(String product_seller) {
        this.product_seller = product_seller;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProduct_address() {
        return product_address;
    }

    public void setProduct_address(String product_address) {
        this.product_address = product_address;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
