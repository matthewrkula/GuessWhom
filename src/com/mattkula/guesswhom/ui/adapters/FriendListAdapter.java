package com.mattkula.guesswhom.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.sromku.simple.fb.entities.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 2/9/14.
 */
public class FriendListAdapter extends BaseAdapter implements Filterable{

    List<Profile> friends;
    List<Profile> friendsFiltered;
    Context c;

    public FriendListAdapter(List<Profile> friends, Context c) {
        this.friends = friends;
        this.friendsFiltered = friends;
        this.c = c;
    }

    @Override
    public int getCount() {
        return friendsFiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return friendsFiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(friendsFiltered.get(i).getId());
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        View v = convertview;
        Profile friend = friendsFiltered.get(i);
        if(v == null)
            v = View.inflate(c, R.layout.listitem_friend, null);

        TextView opponent = (TextView)v.findViewById(R.id.text_friend_name);
        opponent.setText(friend.getName());
        ProfilePictureView picture = (ProfilePictureView)v.findViewById(R.id.friend_profile_picture);
        picture.setPresetSize(ProfilePictureView.SMALL);
        picture.setProfileId(friend.getId());

        if(i % 2 == 0)
            v.setBackgroundColor(0x33ffffff);
        else
            v.setBackgroundColor(0x00ffffff);

        v.setPadding(4, 4, 4, 4);
        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<Profile> myFilter = new ArrayList<Profile>();

                if(charSequence.length() == 0){
                    results.values = friends;
                    results.count = friends.size();
                    return results;
                }

                for(Profile profile : friends){
                    String name = profile.getName();
                    if(name.toLowerCase().contains(charSequence.toString().toLowerCase()))
                        myFilter.add(profile);
                }

                results.values = myFilter;
                results.count = myFilter.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                friendsFiltered = (List<Profile>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
