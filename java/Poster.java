package com.esther.perfect;

public class Poster {

    private String title;
    private String date;
    private String location;
    private String img;
    private String link;

    public Poster() {
        // Default constructor required for calls to DataSnapshot.getValue(Poster.class)
    }

    public Poster(String title, String date, String location, String img, String link) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.img = img;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getImg() {
        return img;
    }

    public String getLink() {
        return link;
    }
}
