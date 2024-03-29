package com.example.medrait.model;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoInfoModel
        extends BaseModel {

    public static final int BUFFER = 1000;

    public long id;
    public long dateuploaded;
    public int views;
    public ContentModel title;
    public ContentModel description;
    public OwnerModel owner;
    //public ContentModel comments;

    public String getFormattedDate() {
        // service short timeStamp veriyor o yuzden bin ile carpiyoruz
        return SimpleDateFormat.getDateInstance().format(new Date(dateuploaded * BUFFER));
    }

}