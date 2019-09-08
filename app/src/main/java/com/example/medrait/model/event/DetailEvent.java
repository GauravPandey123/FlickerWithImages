package com.example.medrait.model.event;


import com.example.medrait.model.PhotoInfoModel;

public class DetailEvent
        extends BaseServiceEvent<PhotoInfoModel> {

    public DetailEvent(PhotoInfoModel item, Throwable exception) {
        super(item, exception);
    }

}