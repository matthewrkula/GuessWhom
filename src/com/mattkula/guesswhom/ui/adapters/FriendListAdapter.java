package com.mattkula.guesswhom.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.sromku.simple.fb.entities.Profile;

import java.util.List;

/**
 * Created by matt on 2/9/14.
 */
public class FriendListAdapter extends BaseAdapter {

    List<Profile> friends;
    Context c;

    public FriendListAdapter(List<Profile> friends, Context c) {
        this.friends = friends;
        this.c = c;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int i) {
        return friends.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(friends.get(i).getId());
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        View v = convertview;
        if(v == null)
            v = View.inflate(c, R.layout.listitem_games, null);

        TextView opponent = (TextView)v.findViewById(R.id.text_opponent);
        opponent.setText(friends.get(i).getName());
        ProfilePictureView picture = (ProfilePictureView)v.findViewById(R.id.game_profile_picture);
        picture.setPresetSize(ProfilePictureView.SMALL);
        picture.setProfileId(friends.get(i).getId());

        return v;
    }
}
