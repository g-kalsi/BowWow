package com.codeninjas.bowwow.models;

public class RemindersModel {
    String title, date, time, message, id;

    public RemindersModel() {
    }

    public RemindersModel(String title, String date, String time, String message, String id) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.message = message;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
