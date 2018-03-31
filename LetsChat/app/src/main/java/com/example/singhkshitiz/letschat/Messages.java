package com.example.singhkshitiz.letschat;

/**
 * Created by KSHITIZ on 3/27/2018.
 * ----CREATED TO WORK WITH "messages" CHILD IN DATABASE----
 */

public class Messages {

    private String message,type;
    private long time;
    private boolean seen;
    private String from;

    public Messages(){

    }
    public Messages(String message, String type, long time, boolean seen,String from) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
