package com.buggycoder.tickmenot.model;

public class WhatsappNotif {

    public final String event;
    public final String sender;
    public final String message;
    public final long postTime;

    public WhatsappNotif(String event, String sender, String message, long postTime) {
        this.event = event;
        this.sender = sender;
        this.message = message;
        this.postTime = postTime;
    }

}
