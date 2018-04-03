package com.example.sachin.eventapps;

/**
 * Created by Sachin on 2018-04-03.
 */

public class Test {

    String desc;
    String title;
    String image;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public Test(String desc, String title, String image) {
        this.desc = desc;
        this.title = title;
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
