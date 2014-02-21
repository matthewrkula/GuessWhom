package com.mattkula.guesswhom.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Answer;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.ConfirmGuessActivity;
import com.squareup.picasso.Picasso;
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
    int fadedMap;
    OnGuessListener listener;

    SpringSystem springSystem;
    final SpringConfig springConfig = new SpringConfig(50, 4);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gameboard, container, false);

        springSystem = SpringSystem.create();

        mGridView = (GridView)v.findViewById(R.id.gridview);
        mGridView.setNumColumns(NUM_COLUMNS);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        mImageWidth = size.x / NUM_COLUMNS;

        if(game != null)
            fadedMap = PreferenceManager.getFadedMap(getActivity(), game.id);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(getActivity());
    }


    public void setGame(Game game) {
        this.game = game;
        fadedMap = PreferenceManager.getFadedMap(getActivity(), game.id);
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
            final View returnView = View.inflate(getActivity(), R.layout.griditem_person, null);
            final int pos = i;

            listener = (OnGuessListener)getActivity();

            final Answer friend = (Answer)getItem(i);
            returnView.setLayoutParams(new AbsListView.LayoutParams(mImageWidth, mImageWidth));

            final ImageView iv = (ImageView)returnView.findViewById(R.id.image_profile_picasso);
            iv.setLayoutParams(new RelativeLayout.LayoutParams(mImageWidth, mImageWidth));

            Picasso.with(getActivity())
                    .load(String.format("https://graph.facebook.com/%s/picture?width=%d&height=%d", friend.fb_id, mImageWidth, mImageWidth))
                    .placeholder(R.drawable.default_user)
                    .into(iv);

            TextView nameView = (TextView)returnView.findViewById(R.id.text_profile_name);
            nameView.setText(friend.name);

            final Spring spring = springSystem.createSpring();
            spring.setSpringConfig(springConfig);

            spring.addListener(new SpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                    float value = (float) spring.getCurrentValue();
                    float scale = 1f - (value * 0.5f);
                    iv.setScaleX(scale);
                    iv.setScaleY(scale);
                }

                @Override
                public void onSpringAtRest(Spring spring) {

                }

                @Override
                public void onSpringActivate(Spring spring) {

                }

                @Override
                public void onSpringEndStateChange(Spring spring) {

                }
            });

            returnView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch(motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            spring.setEndValue(1);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            spring.setEndValue(0);
                            break;
                        case MotionEvent.ACTION_UP:
                            spring.setEndValue(0);

                            if(isFaded(pos))
                                view.animate().alpha(1).start();
                            else
                                view.animate().alpha(0.3f).start();
                            swapBit(pos);
                    }
                    return false;
                }
            });

            returnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            returnView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onGuess(friend);

                    return false;
                }
            });

            returnView.setTag(spring);

            if(isFaded(i))
                returnView.setAlpha(0.3f);

            return returnView;
        }
    }

    private boolean isFaded(int position){
        return ((fadedMap >> position) & 1) > 0;
    }

    private void swapBit(int position){
        fadedMap = fadedMap ^ (1 << position);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(game != null)
            PreferenceManager.setFadedMap(getActivity(), game.id, fadedMap);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(game != null)
            PreferenceManager.setFadedMap(getActivity(), game.id, fadedMap);
    }

    public interface OnGuessListener {
        public void onGuess(Answer answer);
    }
}
