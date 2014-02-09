package com.mattkula.guesswhom.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.ui.GameActivity;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;

/**
 * Created by matt on 2/8/14.
 */
public class AuthorizedMainFragment extends Fragment {

    TextView welcomeText;
    ProfilePictureView profilePictureView;
    Button newGameButton;

    SimpleFacebook simpleFacebook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =  inflater.inflate(R.layout.fragment_main_authorized, null);

        welcomeText = (TextView)v.findViewById(R.id.text_welcome);
        profilePictureView = (ProfilePictureView)v.findViewById(R.id.facebook_profile_picture);
        newGameButton = (Button)v.findViewById(R.id.btn_new_game);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GameActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_authorized, menu);
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
}
