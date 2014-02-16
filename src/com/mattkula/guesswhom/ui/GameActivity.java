package com.mattkula.guesswhom.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.ApplicationController;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.Constants;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.fragments.GameBoardFragment;
import com.sromku.simple.fb.SimpleFacebook;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by Matt on 2/8/14.
 */
public class GameActivity extends FragmentActivity {

    public static final String EXTRA_GAME = "game";

    SimpleFacebook simpleFacebook;
    GameBoardFragment fragment;

    Game game;

    Spinner questionSpinner;
    TextView askerText;
    Button askButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game)getIntent().getSerializableExtra(EXTRA_GAME);
        if(game == null)
            throw new IllegalStateException("No game sent to GameActivity");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);

        fragment = (GameBoardFragment)getSupportFragmentManager().findFragmentById(R.id.game_fragment);

        setUpHeader();
    }

    private void setUpHeader(){
        String myAnswerId;
        String myId = PreferenceManager.getProfileId(this);
        if(game.opponent_id.equals(myId)){
            myAnswerId = game.opponent_answer;
        }else{
            myAnswerId = game.creator_answer;
        }

        ((ProfilePictureView)findViewById(R.id.image_my_answer)).setProfileId(myAnswerId);
        for(int i=0; i < game.answers.length; i++)
            if(game.answers[i].fb_id.equals(myAnswerId))
                ((TextView)findViewById(R.id.text_my_answer_name)).setText(game.answers[i].name);

        askerText = (TextView)findViewById(R.id.text_question_asker);
        questionSpinner = (Spinner)findViewById(R.id.spinner_yes_no);
        askButton = (Button)findViewById(R.id.btn_ask);

        if(myId.equals(game.whose_turn))
            itIsMyTurn();
        else
            itIsTheirTurn();

        ((TextView)findViewById(R.id.text_question_text)).setText(game.question);
    }

    private void itIsTheirTurn(){
        askerText.setText("You asked:");
        questionSpinner.setVisibility(View.INVISIBLE);
        askButton.setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.text_and)).setVisibility(View.INVISIBLE);
    }

    private void itIsMyTurn(){
        askerText.setText("They asked:");
        questionSpinner.setAdapter(new SpinAdapter(getApplicationContext(), 0, new String[]{"Yes", "No"}));
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askQuestion();
            }
        });
    }

    private void askQuestion(){
        final EditText v = new EditText(this);
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Please ask yes/no question.")
                .setView(v)
                .setPositiveButton("Ask", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        progressDialog.setTitle("Sending question...");
                        progressDialog.show();
                        sendQuestion(v.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        d.show();
    }

    private void sendQuestion(String text){
        text = URLEncoder.encode(text);

        String url = String.format("%supdate/%s.json?question=%s&response=%s",
                Constants.BASE_URL,
                game.id,
                text,
                questionSpinner.getSelectedItem().toString());

        Log.e("ASDF", url);
        JsonObjectRequest newGameRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Error sending question", Toast.LENGTH_LONG).show();
                Log.e("ASDF", volleyError.toString());
                volleyError.printStackTrace();
            }
        });

        ApplicationController.getInstance().getRequestQueue().add(newGameRequest);
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
