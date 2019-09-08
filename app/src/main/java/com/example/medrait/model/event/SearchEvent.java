package com.example.medrait.model.event;


import com.example.medrait.model.PhotoModel;

import java.util.List;


public class SearchEvent
        extends BaseServiceEvent<List<PhotoModel>> {

    public SearchEvent(List<PhotoModel> item, Throwable exception) {
        super(item, exception);
    }

}