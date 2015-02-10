package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class TwitterHomeActivity extends ActionBarActivity {
    TweetsAdapter tweetsAdapter;
    Tweet lastTweet = new Tweet();
    int lastPage = 0;
    ListView lvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_home);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff21d3ff));
        getSupportActionBar().setElevation(5);
        lastTweet.setTweetId(Long.MAX_VALUE);
        tweetsAdapter = new TweetsAdapter(this);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lvTweets.setAdapter(tweetsAdapter);
        getData(0);
    }

    @Override
    public void onBackPressed() {
        RestApplication.getRestClient().clearAccessToken();
        NavUtils.navigateUpFromSameTask(this);
    }

    private void getData(final int page) {
        if(page == 0) {
            lvTweets.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    getData(page);
                }
            });
        }
        Log.e(this.getClass().getName(), "calling api; page " + page + " lastTweet: " + lastTweet.getTweetId());
        RestApplication.getRestClient().getHomeTimeline(page, lastTweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TwitterHomeActivity.class.getName(), "Getting tweets from DB now ", throwable);
                if (page == 0) {
                    tweetsAdapter.clear();
                }
                tweetsAdapter.addAll(Tweet.recentItems(page == 0 ? lastTweet.getTweetId() : lastTweet.getTweetId() - 1));
                if (!tweetsAdapter.isEmpty()) {
                    lastTweet.setTweetId(tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getTweetId());
                    Log.e(TwitterHomeActivity.class.getName(), "Got tweets from DB upto " + lastTweet.getTweetId());
                    tweetsAdapter.notifyDataSetChanged();
                }
                Toast.makeText(TwitterHomeActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                try {
                    List<Tweet> tweets = new ArrayList<>();
                    Log.e(TwitterHomeActivity.class.getName(), "Tweet size is : " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        Tweet tweet = new Tweet();
                        tweet.setTweetId(data.getLong("id"));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
                        dateFormat.setLenient(true);
                        tweet.setBody(data.getString("text"));
                        Log.e(TwitterHomeActivity.class.getName(), "Tweet is: " + tweet.getBody());
                        tweet.setTime(dateFormat.parse(data.getString("created_at")).getTime());

                        JSONObject user = data.getJSONObject("user");
                        tweet.setName(user.getString("name"));
                        tweet.setUsername(user.getString("screen_name"));
                        tweet.setPhoto(user.getString("profile_image_url"));
                        Tweet.delete(tweet.getTweetId());
                        tweet.save();
                        tweets.add(tweet);
                    }
                    if (lastTweet.getTweetId() < Long.MAX_VALUE && !tweets.contains(lastTweet) && page == 0) {
                        Log.e(TwitterHomeActivity.class.getName(), "Adding recent tweet from DB");
                        tweets.add(0, Tweet.getTweet(lastTweet.getTweetId()));
                    }
                    if (!tweets.isEmpty()) {
                        lastTweet.setTweetId(tweets.get(tweets.size() - 1).getTweetId());
                    }
                    if (page == 0) {
                        tweetsAdapter.clear();
                    }
                    tweetsAdapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tweetsAdapter.notifyDataSetChanged();
            }
        });
        lastPage = page;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitter_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.miLogout:
                RestApplication.getRestClient().clearAccessToken();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.miTweet:
                Intent intent = new Intent(this, TweetActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.miRefresh:
                lastTweet.setTweetId(Long.MAX_VALUE);
                getData(0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 0) {
            long tweetId = data.getExtras().getLong("TWEET_ID");
            Log.e(this.getClass().getName(), "new tweetId is " + tweetId);
            lastTweet.setTweetId(tweetId);
            getData(0);
        }
    }
}
