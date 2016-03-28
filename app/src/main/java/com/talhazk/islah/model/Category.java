package com.talhazk.islah.model;

/**
 * Created by Talhazk on 05-Mar-16.
 */
public class Category {


    //private String imgPath;
    private  int id;
    private String name;
    String logo;
    String image;



    public Category(int id,  String name, String logo, String image) {
        this.id = id;
        this.image = image;
        this.logo = logo;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
