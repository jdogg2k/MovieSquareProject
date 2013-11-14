package com.jorose.moviesquare;

/**
 * Created by jrose on 11/8/13.
 */
public class Movie {
    private int id;
    private String title;
    private String fan_id;
    private String theater_id;
    private float rating;
    private String dateStr;
    private Venue venue;

    public Movie(){}

    public Movie(String title, String fan_id, String theater_id, float rating, String dateStr, Venue venue) {
        super();
        this.title = title;
        this.fan_id = fan_id;
        this.theater_id = theater_id;
        this.rating = rating;
        this.dateStr = dateStr;
        this.venue = venue;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", fan_id=" + fan_id + ", theater_id=" + theater_id + ", rating=" + rating + ", dateStr=" + dateStr + ", venue=" + venue + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int movieID) {
        id = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titleParam) {
        title = titleParam;
    }

    public String getTheater_id() {
        return theater_id;
    }

    public void setTheater_id(String theater_idParam) {
        theater_id = theater_idParam;
    }

    public String getFan_id() {
        return fan_id;
    }

    public void setFan_id(String fan_idParam) {
       fan_id = fan_idParam;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float ratingParam) {
        rating = ratingParam;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStrParam) {
        dateStr = dateStrParam;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue ven) {
        venue = ven;
    }
}
