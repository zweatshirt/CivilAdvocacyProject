package com.zach.civiladvocacy;

import java.io.Serializable;

// Example query:  https://www.googleapis.com/civicinfo/v2/representatives?key=Your-API-Key&address=address
public class Official implements Serializable {

    // Address strings for location display
    private String line;

    private String city;
    private String state;
    private String zip;
    private String normalizedAddress;
    // Office title
    private String officeTitle;
    private String name;
    private int officeIndex; // not necessary but just in case
    private String party;

    private String phone;
    private String webUrl;
    private String email;
    private String photoUrl; // if null, implement placeholder photo
    private String facebookId;
    private String twitterId;
    private String youtubeId;

    /* Gross */
    public String getLine() {
        return line;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getOfficeTitle() {
        return officeTitle;
    }

    public String getName() { return name; }

    public int getOfficeIndex() {
        return officeIndex;
    }

    public String getParty() {
        return party;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setOfficeTitle(String officeTitle) {
        this.officeTitle = officeTitle;
    }

    public void setName(String name) { this.name = name; }

    public void setOfficeIndex(int officeIndex) {
        this.officeIndex = officeIndex;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }
}
