package com.codeninjas.bowwow.models;

public class FeedsModel {

    String uid, name, profilePic, feed, type, key;
    boolean privacy;
    long created_at;

    public FeedsModel() {
    }

    public FeedsModel(String uid, String name, String profilePic, String feed, String type, boolean privacy, long created_at, String key) {
        this.uid = uid;
        this.name = name;
        this.profilePic = profilePic;
        this.feed = feed;
        this.type = type;
        this.privacy = privacy;
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

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
