package com.mattkula.guesswhom.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mattkula.guesswhom.ApplicationController;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.Constants;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Answer;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.FriendPickerActivity;
import com.mattkula.guesswhom.ui.GameActivity;
import com.mattkula.guesswhom.ui.adapters.MyGamesAdapter;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matt on 2/8/14.
 */
public class AuthorizedMainFragment extends Fragment {

    public static final int FRIEND_PICKER_CODE = 1;

    TextView welcomeText;
    ProfilePictureView profilePictureView;
    Button newGameButton;

    SimpleFacebook simpleFacebook;

    Game[] games;
    ListView listMyGames;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =  inflater.inflate(R.layout.fragment_main_authorized, null);

        welcomeText = (TextView)v.findViewById(R.id.text_welcome);
        profilePictureView = (ProfilePictureView)v.findViewById(R.id.facebook_profile_picture);
        listMyGames = (ListView)v.findViewById(R.id.list_my_games);
        listMyGames.setOnItemClickListener(listener);

        newGameButton = (Button)v.findViewById(R.id.btn_new_game);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FriendPickerActivity.class);
                startActivityForResult(i, FRIEND_PICKER_CODE);
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(getActivity());
        if(PreferenceManager.isLoggedIn(getActivity())){
            welcomeText.setText("Welcome to Guess Whom, " + PreferenceManager.getFirstName(getActivity()) + ".");
            profilePictureView.setProfileId(PreferenceManager.getProfileId(getActivity()));
        }
        makeMeRequest();
        getMyGames();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startGameActivity(games[i]);
            }
        };

    private void startGameActivity(Game game){
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_GAME, game);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FRIEND_PICKER_CODE){
            if(resultCode == Activity.RESULT_OK){
                String id =  data.getExtras().getString(FriendPickerActivity.FRIEND_PICKER_EXTRA_ID);
                String name = data.getExtras().getString(FriendPickerActivity.FRIEND_PICKER_EXTRA_NAME);
                showNewGameDialog(id, name);
            }
        }
    }

    private void showNewGameDialog(final String id, String name){
        View v = View.inflate(getActivity(), R.layout.alert_newgame, null);

        ProfilePictureView picture = (ProfilePictureView)v.findViewById(R.id.alert_profile_picture);
        picture.setProfileId(id);

        AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle("New Game with " + name + "?")
                .setView(v)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        progressDialog.setTitle("Creating game...");
                        progressDialog.show();
                        createNewGame(id);
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

    public void createNewGame(String opponentId){
        String url = String.format("%snew_game.json?opponent_id=%s&access_token=%s",
                Constants.BASE_URL,
                opponentId,
                simpleFacebook.getAccessToken());

        JsonObjectRequest newGameRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Game game = getGame(response);
                if (game != null)
                    startGameActivity(game);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e("ASDF", volleyError.toString());
            }
        });

        ApplicationController.getInstance().getRequestQueue().add(newGameRequest);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_authorized, menu);
    }

    private void makeMeRequest(){

        simpleFacebook.getProfile(new SimpleFacebook.OnProfileRequestListener() {

            @Override
            public void onComplete(Profile profile) {
                profilePictureView.setProfileId(profile.getId());
                welcomeText.setText("Welcome to Guess Whom, " + profile.getFirstName() + ".");
                PreferenceManager.setFirstName(getActivity(), profile.getFirstName());
                PreferenceManager.setProfileId(getActivity(), profile.getId());
            }

            @Override
            public void onThinking() {}

            @Override
            public void onException(Throwable throwable) {}

            @Override
            public void onFail(String reason) {}
        });
    }

    private void getMyGames(){
        String url = String.format("%s%s?user_id=%s", Constants.BASE_URL, "games.json", PreferenceManager.getProfileId(getActivity()));

//        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                games = new Game[response.length()];
//                for(int i=0; i < response.length(); i++){
//
//                    try {
//                        games[i] = getGame(response.getJSONObject(i));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    listMyGames.setAdapter(new MyGamesAdapter(getActivity(), games));
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        });
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                games = gson.fromJson(s, Game[].class);

                listMyGames.setAdapter(new MyGamesAdapter(getActivity(), games));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        ApplicationController.getInstance().getRequestQueue().add(request);
    }

    private Game getGame(JSONObject obj){
        Game game;

        try {
            String id = obj.getString("id");
            String opponent_id = obj.getString("opponent_id");
            String lastQuestion = obj.getString("lastquestion");
            String question = obj.getString("question");
            String gameResponse = obj.getString("response");
            String whose_turn = obj.getString("whose_turn");
            String creator_answer = obj.getString("creator_answer");
            String opponent_answer = obj.getString("opponent_answer");
            JSONArray example_answers_arr = obj.getJSONArray("answers");
            Answer[] example_answers =  new Answer[example_answers_arr.length()];

            for(int i=0; i < example_answers.length; i++){
                JSONObject answer = example_answers_arr.getJSONObject(i);
                String name = answer.getString("name");
                String fb_id = answer.getString("fb_id");
                example_answers[i] = new Answer(name, fb_id);
            }


            game = new Game(id, opponent_id, lastQuestion, question, gameResponse, whose_turn, creator_answer, opponent_answer, example_answers);
        } catch (Exception e){
            e.printStackTrace();
            game = null;
        }
        return game;
    }
}
