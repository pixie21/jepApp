package com.example.jepapp.Models;

public class Cart {
    String cost;
    String image;
    String ordertitle;
    String quantity;
    String type;
    String username;

    public Cart(String cost, String image, String ordertitle, String quantity, String type, String username) {
        this.cost = cost;
        this.image = image;
        this.ordertitle = ordertitle;
        this.quantity = quantity;
        this.type = type;
        this.username = username;
    }

    public Cart() {
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrdertitle() {
        return ordertitle;
    }

    public void setOrdertitle(String ordertitle) {
        this.ordertitle = ordertitle;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}