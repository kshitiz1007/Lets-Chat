package com.example.singhkshitiz.letschat;

/**
 * Created by KSHITIZ on 3/20/2018.
 * ------THIS CLASS IS FOR FETCHING A USER DATA-----
 */

public class Users {
    public String name;
    public String status;
    public String image;
    public String thumb_image;
    Users(){ }

    public Users(String name, String status, String image,String thumb_image) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumb_image=thumb_image;
    }

    public String getName() { return name; }


    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbImage() {
        return thumb_image;
    }

    public void setThumbImage(String thumbImage) {
        this.thumb_image = thumb_image;
    }


}
