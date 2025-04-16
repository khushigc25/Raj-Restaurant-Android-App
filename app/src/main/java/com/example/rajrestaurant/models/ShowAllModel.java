package com.example.rajrestaurant.models;

import java.io.Serializable;

public class ShowAllModel implements Serializable {
    String description;
    String name;
    String vn;
    int price;
    String img_url;
    String type;

    public ShowAllModel() {
    }

    public ShowAllModel(String description, String name, String vn, int price, String img_url, String type) {
        this.description = description;
        this.name = name;
        this.vn = vn;
        this.price = price;
        this.img_url = img_url;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVn() {
        return vn;
    }

    public void setVn(String rating) {
        this.vn = vn;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
