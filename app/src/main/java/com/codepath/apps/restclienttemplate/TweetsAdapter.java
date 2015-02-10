package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by alinaqvi on 2/8/15.
 */
public class TweetsAdapter extends ArrayAdapter<Tweet> {

    public TweetsAdapter(Context context) {
        super(context, R.layout.item_tweet);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        ImageView ivUser = (ImageView) convertView.findViewById(R.id.ivUser);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        Log.e(TweetsAdapter.class.getName(), "position is: " + position);
        Log.e(TweetsAdapter.class.getName(), "Photo is: " + tweet.getPhoto());
        Picasso.with(getContext()).load(tweet.getPhoto()).into(ivUser);
        tvName.setText(tweet.getName());
        tvUser.setText("@" + tweet.getUsername());
        tvTime.setText(DateUtils.getRelativeTimeSpanString(tweet.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString().replaceFirst(".*([0-9]+) (.).*", "$1$2"));
        tvBody.setText(tweet.getBody());
        return convertView;
    }
}
