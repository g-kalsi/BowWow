package com.codeninjas.bowwow.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class NotesModel {

    String title, note, key;
    long created_at;

    public NotesModel() {

    }

    public NotesModel(String title, String note, String key, long created_at) {
        this.title = title;
        this.note = note;
        this.key = key;
        this.created_at = created_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("note", note);
        result.put("title", title);
        result.put("created_at", created_at);

        return result;
    }

}
