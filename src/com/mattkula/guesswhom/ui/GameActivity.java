package com.mattkula.guesswhom.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.mattkula.guesswhom.data.models.Answer;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.fragments.GameBoardFragment;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by Matt on 2/8/14.
 */
public class GameActivity extends FragmentActivity implements GameBoardFragment.OnGuessListener {

    public static final String EXTRA_GAME = "game";

    SimpleFacebook simpleFacebook;
    GameBoardFragment fragment;

    Game game;

    Button askButton;
    TextView hideText;
    TextView replyText;
    TextView questionText;
    ProgressDialog progressDialog;

    String myAnswerId;
    String otherAnswerId;

    boolean myTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game)getIntent().getSerializableExtra(EXTRA_GAME);
        if(game == null)
            throw new IllegalStateException("No game sent to GameActivity");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();

        progressDialog = new ProgressDialog(this);

        fragment = (GameBoardFragment)getSupportFragmentManager().findFragmentById(R.id.game_fragment);

        setUpHeader();
        fragment.setGame(game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance();
    }

    private void setUpHeader(){
        String myId = PreferenceManager.getProfileId(this);
        if(game.opponent_id.equals(myId)){
            myAnswerId = game.opponent_answer;
            otherAnswerId = game.creator_answer;
        }else{
            myAnswerId = game.creator_answer;
            otherAnswerId = game.opponent_answer;
        }

        ImageView iv = (ImageView)findViewById(R.id.image_my_answer);

        Picasso.with(this)
                .load(String.format("https://graph.facebook.com/%s/picture?width=%d&height=%d", myAnswerId, iv.getWidth(), iv.getHeight()))
                .placeholder(R.drawable.default_user)
                .into(iv);

        for(int i=0; i < game.answers.length; i++)
            if(game.answers[i].fb_id.equals(myAnswerId))
                ((TextView)findViewById(R.id.text_my_answer_name)).setText(game.answers[i].name);

        askButton = (Button)findViewById(R.id.btn_ask);
        replyText = (TextView)findViewById(R.id.text_reply);
        questionText = (TextView)findViewById(R.id.text_question_text);
        hideText = (TextView)findViewById(R.id.hide_view);

        hideText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        view.animate().alpha(0).start();
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.animate().alpha(1).start();
                        return true;
                }
                return false;
            }
        });

        if(myId.equals(game.whose_turn))
            itIsMyTurn();
        else
            itIsTheirTurn();

        if(game.turn_count == 0){
            askButton.setText("ASK");
            questionText.setVisibility(View.GONE);
            replyText.setVisibility(View.GONE);
        }else if(game.turn_count == 1){
            replyText.setVisibility(View.GONE);
        }
    }

    private void itIsTheirTurn(){
        myTurn = false;
        questionText.setText(String.format("You asked \"%s\".", game.question));
        askButton.setVisibility(View.INVISIBLE);
        replyText.setText(String.format("You answered \"%s\" to \"%s\".", game.response, game.lastquestion));
    }

    private void itIsMyTurn(){
        myTurn = true;
        questionText.setText(String.format("They asked \"%s\".", game.question));
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askQuestion();
            }
        });
        replyText.setText(String.format("They answered \"%s\" to \"%s\".", game.response, game.lastquestion));
    }

    private void askQuestion(){
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.alert_reply, null, false);
        TextView question = (TextView)layout.findViewById(R.id.text_question);
        final EditText newQuestion = (EditText)layout.findViewById(R.id.edit_new_question);
        final EditText answer = (EditText)layout.findViewById(R.id.edit_response);

        if(game.turn_count == 0){
            question.setVisibility(View.GONE);
            answer.setVisibility(View.GONE);
        }

        question.setText(game.question);
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Ask your question.")
                .setView(layout)
                .setPositiveButton("Ask", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        progressDialog.setTitle("Sending question...");
                        progressDialog.show();
                        sendQuestion(newQuestion.getText().toString(), answer.getText().toString(), true, false);
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

    private void sendQuestion(String text, String answer, final boolean closeAfter, boolean isCompleted){
        text = URLEncoder.encode(text);
        answer = URLEncoder.encode(answer);

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
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if(closeAfter)
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
    public void onGuess(final Answer answer) {

        if(!myTurn){
            Toast.makeText(this, "Wait your turn!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent i = new Intent(this, ConfirmGuessActivity.class);
        i.putExtra(ConfirmGuessActivity.EXTRA_URL, String.format("https://graph.facebook.com/%s/picture?width=%d&height=%d", answer.fb_id, 200, 200));
        i.putExtra(ConfirmGuessActivity.EXTRA_ANSWER, answer);
        i.putExtra(ConfirmGuessActivity.EXTRA_GAME, game);
        i.putExtra(ConfirmGuessActivity.EXTRA_CORRECT, answer.fb_id.equals(otherAnswerId));
        startActivityForResult(i, 23);
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 23){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
