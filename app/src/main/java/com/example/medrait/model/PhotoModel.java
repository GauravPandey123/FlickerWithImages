package com.example.medrait.model;


public class PhotoModel
        extends BaseModel {


    private static final String IMAGE_URL = "https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";

    public long id;
    public String secret;
    public String server;
    public int farm;
    public String  title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getImageUrl(ImageSize size) {
        return String.format(IMAGE_URL, farm, server, id, secret, size.getValue());
    }

}