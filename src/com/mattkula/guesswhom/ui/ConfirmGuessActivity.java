package com.mattkula.guesswhom.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mattkula.guesswhom.ApplicationController;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.Constants;
import com.mattkula.guesswhom.data.models.Answer;
import com.mattkula.guesswhom.data.models.Game;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by matt on 2/18/14.
 */
public class ConfirmGuessActivity extends Activity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_ANSWER = "answer";
    public static final String EXTRA_GAME = "game";
    public static final String EXTRA_CORRECT = "correct";

    EditText answerEdit;
    Button confirmNoBtn;
    Button confirmYesBtn;
    ImageView profilePicture;
    TextView confirmNameText;

    boolean answerEditShowing = false;
    boolean answerIsCorrect;

    Game game;
    Answer answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmguess);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString(EXTRA_URL);
        answer = (Answer)extras.getSerializable(EXTRA_ANSWER);
        game = (Game)extras.getSerializable(EXTRA_GAME);

        profilePicture = (ImageView)findViewById(R.id.image_confirm_profile);


        answerEdit = (EditText)findViewById(R.id.edit_confirm_question_answer);
        answerEdit.clearFocus();
        profilePicture.requestFocus();

        confirmNameText = (TextView)findViewById(R.id.text_confirm_name);
        confirmNameText.setText("Do you want to guess " + answer.name + "?");

        confirmYesBtn = (Button)findViewById(R.id.btn_confirm_yes);
        confirmYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerEditShowing)
                    sendRequest();
                else
                    showAnswerBox();

            }
        });

        confirmNoBtn = (Button)findViewById(R.id.btn_confirm_no);
        confirmNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Picasso.with(this)
                .load(url)
                .into(profilePicture);

        confirmNameText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                confirmNameText.getViewTreeObserver().removeOnPreDrawListener(this);

                ((ViewGroup)confirmNameText.getParent()).setAlpha(0);

                fadeIn();
                return true;
            }
        });
    }

    private void fadeIn(){
        ((ViewGroup)confirmNameText.getParent()).animate().alpha(1);
    }

    private void showAnswerBox(){
        int height = answerEdit.getHeight() + 15;
        answerEdit.requestFocus();

        AnimatorSet set = new AnimatorSet();
        Animator a = ObjectAnimator.ofFloat(profilePicture, "translationY", 0f, height);
        Animator b = ObjectAnimator.ofFloat(confirmYesBtn, "translationY", 0f, height);
        Animator c = ObjectAnimator.ofFloat(confirmNoBtn, "translationY", 0f, height);
        Animator d = ObjectAnimator.ofFloat(answerEdit, "alpha", 0f, 1f);
        set.playTogether(a, b, c, d);
        set.start();

        answerEditShowing = true;
    }

    private void sendRequest(){
        if(answerEdit.getText().toString().length() == 0){
            return;
        }

        sendGuess("Is it " + answer.name + "?", false);
    }

    private void sendGuess(String text, boolean isCompleted){
        text = URLEncoder.encode(text);
        String answer = URLEncoder.encode(answerEdit.getText().toString());

        String url = String.format("%supdate/%s.json?question=%s&response=%s",
                Constants.BASE_URL,
                game.id,
                text,
                answer);

        if(isCompleted)
            url += "&is_completed=true";

        Log.e("ASDF", url);
        JsonObjectRequest newGameRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Error sending question", Toast.LENGTH_LONG).show();
                Log.e("ASDF", volleyError.toString());
                volleyError.printStackTrace();
            }
        });

        ApplicationController.getInstance().getRequestQueue().add(newGameRequest);
    }

    @Override
    public void finish() {
        ((ViewGroup)confirmNameText.getParent()).animate().alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ConfirmGuessActivity.super.finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}
