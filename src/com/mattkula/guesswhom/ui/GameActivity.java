package com.mattkula.guesswhom.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.fragments.GameBoardFragment;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by Matt on 2/8/14.
 */
public class GameActivity extends FragmentActivity {

    public static final String EXTRA_GAME = "game";

    SimpleFacebook simpleFacebook;
    GameBoardFragment fragment;

    Game game;

    Spinner questionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game)getIntent().getSerializableExtra(EXTRA_GAME);
        if(game == null)
            throw new IllegalStateException("No game sent to GameActivity");

        getActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = (GameBoardFragment)getSupportFragmentManager().findFragmentById(R.id.game_fragment);

        setUpHeader();

    }

    private void setUpHeader(){
        String myId;
        if(game.opponent_id.equals(PreferenceManager.getProfileId(this))){
            myId = game.opponent_id;
        }else{
            myId = game.my_answer;
        }
        ((ProfilePictureView)findViewById(R.id.image_my_answer)).setProfileId(myId);

        for(int i=0; i < game.answers.length; i++)
            if(game.answers[i].fb_id.equals(myId))
                ((TextView)findViewById(R.id.text_my_answer_name)).setText(game.answers[i].name);

        if(myId.equals(game.whose_turn))
            ((TextView)findViewById(R.id.text_question_asker)).setText("They asked:");
        else
            ((TextView)findViewById(R.id.text_question_asker)).setText("You asked:");
        ((TextView)findViewById(R.id.text_question_text)).setText(game.question);
        questionSpinner = (Spinner)findViewById(R.id.spinner_yes_no);
        questionSpinner.setAdapter(new SpinAdapter(getApplicationContext(), 0, new String[]{"Yes", "No"}));
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance();
        fragment.setGame(game);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class SpinAdapter extends ArrayAdapter<String> {
        public SpinAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(getItem(position));
            tv.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50));
            tv.setTextSize(20);
            tv.setTextColor(0xff000000);
            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(getItem(position));
            tv.setLayoutParams(new AbsListView.LayoutParams(150, 100));
            tv.setTextSize(20);
            tv.setTextColor(0xff000000);
            tv.setPadding(20, 20, 0, 0);
            return tv;
        }
    }
}
