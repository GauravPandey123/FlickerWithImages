package com.example.medrait.model.event;


public class BaseServiceEvent<T> {

    public final T item;
    public final Throwable exception;

    public BaseServiceEvent(T item, Throwable exception) {
        this.item = item;
        this.exception = exception;
    }

}