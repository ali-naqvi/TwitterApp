package com.codepath.apps.restclienttemplate.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
@Table(name = "tweets")
public class Tweet extends Model {
    @Column(name = "tweetId", index = true)
    private Long tweetId;
    // Define table fields
    @Column(name = "photo")
    private String photo;
    @Column(name = "name")
    private String name;
    @Column(name = "username")
    private String username;
    @Column(name = "body")
    private String body;
    @Column(name = "time")
    private Long time;

    public Tweet() {
        super();
    }

    // Parse model from JSON
    public Tweet(JSONObject object) {
        super();

        try {
            this.name = object.getString("name");
            this.photo = object.getString("photo");
            this.username = object.getString("username");
            this.body = object.getString("body");
            this.time = object.getLong("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUsername() {
        return username;
    }

    public String getBody() {
        return body;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public static void delete(long tweetId) {
        new Delete().from(Tweet.class).where("tweetId = ?", tweetId).execute();
    }

    // Record Finders
    public static Tweet getTweet(long tweetId) {
        return new Select().from(Tweet.class).where("tweetId = ?", tweetId).executeSingle();
    }

    public static List<Tweet> recentItems(long max_id) {
        return new Select().from(Tweet.class).where("tweetId <= ?", max_id).orderBy("time DESC").limit("10").execute();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Tweet && ((Tweet) o).getTweetId().equals(this.getTweetId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + tweetId.hashCode();
        return result;
    }
}
