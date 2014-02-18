package com.mattkula.guesswhom.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mattkula.guesswhom.ApplicationController;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.Constants;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.FriendPickerActivity;
import com.mattkula.guesswhom.ui.GameActivity;
import com.mattkula.guesswhom.ui.adapters.MyGamesAdapter;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import org.json.JSONObject;

/**
 * Created by matt on 2/8/14.
 */
public class AuthorizedMainFragment extends Fragment {

    public static final int FRIEND_PICKER_CODE = 1;

    Button newGameButton;
    TextView welcomeText;
    ListView listMyGames;
    ProgressDialog progressDialog;
    ProfilePictureView profilePictureView;

    Gson gson;
    SimpleFacebook simpleFacebook;

    Game[] games;
    boolean gamesLoaded = false;
    String mFirstName;
    String mId;


    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            startGameActivity(games[i]);
        }

    };

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
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(getActivity());

        if(PreferenceManager.getProfileId(getActivity()).equals("-1"))
            makeMeRequest();
        else {
            mFirstName = PreferenceManager.getFirstName(getActivity());
            mId = PreferenceManager.getProfileId(getActivity());
            getMyGames();
            profilePictureView.setProfileId(mId);
            welcomeText.setText("Welcome to Guess Whom, " + mFirstName + ".");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_authorized, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_refresh:
                getMyGames();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    private void showNewGameDialog(final String id, final String name){
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
                        updateProgressDialog(true, "Creating game...");
                        createNewGame(id, name);
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

    public void createNewGame(String opponentId, final String opponentName){
        String url = String.format("%snew_game.json?opponent_id=%s&access_token=%s",
                Constants.BASE_URL,
                opponentId,
                simpleFacebook.getAccessToken());

        Request newGameRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                updateProgressDialog(false, null);

                Game game = gson.fromJson(response.toString(), Game.class);
                if (game != null){
                    PreferenceManager.setOpponentName(getActivity(), game.id, opponentName);
                    startGameActivity(game);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                updateProgressDialog(false, null);
            }
        }).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().getRequestQueue().add(newGameRequest);
    }

    private void makeMeRequest(){
        welcomeText.setVisibility(View.INVISIBLE);

        simpleFacebook.getProfile(new SimpleFacebook.OnProfileRequestListener() {

            @Override
            public void onComplete(Profile profile) {
                PreferenceManager.setFirstName(getActivity(), profile.getFirstName());
                PreferenceManager.setProfileId(getActivity(), profile.getId());
                mFirstName = profile.getFirstName();
                mId = profile.getId();
                profilePictureView.setProfileId(mId);
                welcomeText.setText("Welcome to Guess Whom, " + mFirstName + ".");
                welcomeText.setVisibility(View.VISIBLE);
                getMyGames();
            }

            @Override
            public void onThinking() {}

            @Override
            public void onException(Throwable throwable) {}

            @Override
            public void onFail(String reason) {}
        });
    }

    public void getMyGames(){
        String url = String.format("%s%s?user_id=%s", Constants.BASE_URL, "games.json", mId);
        updateProgressDialog(true, "Getting games");
        Log.e("ASDF", url);

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                games = gson.fromJson(s, Game[].class);
                updateProgressDialog(false, null);

                listMyGames.setAdapter(new MyGamesAdapter(getActivity(), games));
                gamesLoaded = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                updateProgressDialog(false, null);
            }
        });

        ApplicationController.getInstance().getRequestQueue().add(request);
    }

    // true = show, false hide, title can be null
    private void updateProgressDialog(boolean show, String title){
       if(progressDialog != null){
           if(show){
               if(title != null)
                   progressDialog.setTitle(title);
               progressDialog.show();
           }
           else
               progressDialog.dismiss();
       }
    }
}
