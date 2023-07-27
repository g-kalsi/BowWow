package com.codeninjas.bowwow.models;

public class ReportsModel {

    String uid, name, profilePic, report, type, imageUrl, key;
    long created_at;

    public ReportsModel() {
    }

    public ReportsModel(String uid, String name, String profilePic, String report, String type, String imageUrl, long created_at, String key) {
        this.uid = uid;
        this.name = name;
        this.profilePic = profilePic;
        this.report = report;
        this.type = type;
        this.imageUrl = imageUrl;
        this.created_at = created_at;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
