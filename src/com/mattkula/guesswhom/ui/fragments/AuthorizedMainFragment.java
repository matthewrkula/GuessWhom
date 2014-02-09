package com.mattkula.guesswhom.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.ui.GameActivity;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;

/**
 * Created by matt on 2/8/14.
 */
public class AuthorizedMainFragment extends Fragment {

    ProfilePictureView profilePictureView;

    SimpleFacebook simpleFacebook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =  inflater.inflate(R.layout.fragment_main_authorized, null);

        profilePictureView = (ProfilePictureView)v.findViewById(R.id.facebook_profile_picture);
        profilePictureView.setOnClickListener(new View.OnClickListener() {
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
        inflater.inflate(R.menu.fragment_authorized, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(getActivity());
        makeMeRequest();
    }

    private void makeMeRequest(){
        simpleFacebook.getProfile(new SimpleFacebook.OnProfileRequestListener() {

            @Override
            public void onComplete(Profile profile) {
                profilePictureView.setProfileId(profile.getId());
            }

            @Override
            public void onThinking() {

            }

            @Override
            public void onException(Throwable throwable) {

            }

            @Override
            public void onFail(String reason) {

            }
        });
    }
}
