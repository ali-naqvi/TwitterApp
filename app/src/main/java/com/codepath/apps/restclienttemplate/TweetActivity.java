package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class TweetActivity extends ActionBarActivity {
    EditText etTweet;
    Button btTweet;
    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff21d3ff));
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btTweet = (Button) findViewById(R.id.btTweet);
        tvCount = (TextView) findViewById(R.id.tvCount);
        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestApplication.getRestClient().postTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TwitterHomeActivity.class.getName(), "Cannot tweet ", throwable);
                        Toast.makeText(TweetActivity.this, "Failed to post this tweet", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet tweet = new Tweet();
                            tweet.setTweetId(response.getLong("id"));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
                            dateFormat.setLenient(true);
                            tweet.setBody(response.getString("text"));
                            Log.e(TweetActivity.class.getName(), "Tweet is: " + tweet.getBody());
                            tweet.setTime(dateFormat.parse(response.getString("created_at")).getTime());

                            JSONObject user = response.getJSONObject("user");
                            tweet.setName(user.getString("name"));
                            tweet.setUsername(user.getString("screen_name"));
                            tweet.setPhoto(user.getString("profile_image_url"));
                            Log.e(TweetActivity.class.getName(), "Photo is: " + tweet.getPhoto());

                            tweet.save();
                            Intent intent = new Intent();
                            intent.putExtra("TWEET_ID", tweet.getTweetId());
                            TweetActivity.this.setResult(0, intent);
                            Log.e(TwitterHomeActivity.class.getName(), "Tweet saved: " + tweet.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        TweetActivity.this.finish();
                    }
                });


            }
        });
        etTweet = (EditText) findViewById(R.id.etTweet);
        tvCount.setText("" + (140 - etTweet.length()));
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = 140 - etTweet.length();
                tvCount.setText("" + (length >= 0 ? length : Html.fromHtml("<span style='color:red'>" + length + "<span>")));
                if (length >= 0) {
                    tvCount.setText("" + length);
                    btTweet.setEnabled(true);
                    btTweet.setBackgroundColor(0xFF292f33);
                } else {
                    tvCount.setText(Html.fromHtml("<font color='red'>" + length + "<font>"));
                    btTweet.setEnabled(false);
                    btTweet.setBackgroundColor(0xFFC2C2C2);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("TWEET_ID", Long.MAX_VALUE);
        TweetActivity.this.setResult(1, intent);
        TweetActivity.this.finish();
    }

}
