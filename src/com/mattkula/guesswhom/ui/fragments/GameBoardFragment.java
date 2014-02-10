package com.mattkula.guesswhom.ui.fragments;

import android.support.v4.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.models.Answer;
import com.mattkula.guesswhom.data.models.Game;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;

import java.util.List;

/**
 * Created by Matt on 2/8/14.
 */
public class GameBoardFragment extends Fragment {
    public final int NUM_COLUMNS = 4;
    public final int NUM_CHOICES = 24;

    GridView mGridView;
    int mImageWidth;

    SimpleFacebook simpleFacebook;

    Game game;

    boolean[] isFaded = new boolean[24];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gameboard, container, false);

        mGridView = (GridView)v.findViewById(R.id.gridview);
        mGridView.setNumColumns(NUM_COLUMNS);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(view.getAlpha() < 0.5)
                    view.animate().alpha(1).start();
                else
                    view.animate().alpha(0.3f).start();

                isFaded[i] = !isFaded[i];
            }
        });

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        mImageWidth = size.x / NUM_COLUMNS;
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(getActivity());
    }

    public void setGame(Game game) {
        this.game = game;
        mGridView.setAdapter(new GameBoardAdapter());
    }

    private class GameBoardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return NUM_CHOICES;
        }

        @Override
        public Object getItem(int i) {
            return game.answers[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View returnView = View.inflate(getActivity(), R.layout.griditem_person, null);
            Answer friend = (Answer)getItem(i);
            returnView.setLayoutParams(new AbsListView.LayoutParams(mImageWidth, mImageWidth));

            ProfilePictureView pictureView = (ProfilePictureView)returnView.findViewById(R.id.image_profile_picture);
            pictureView.setLayoutParams(new RelativeLayout.LayoutParams(mImageWidth, mImageWidth));
            pictureView.setProfileId(friend.fb_id);

            TextView nameView = (TextView)returnView.findViewById(R.id.text_profile_name);
            nameView.setText(friend.name);

            if(isFaded[i])
                returnView.setAlpha(0.3f);
            return returnView;
        }
    }
}
