package com.mattkula.guesswhom.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.mattkula.guesswhom.R;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by matt on 2/8/14.
 */
public class UnauthorizedMainFragment extends Fragment {

    SimpleFacebook mSimpleFacebook;
    SimpleFacebook.OnLoginListener mOnLoginListener;

    Button mLoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_unauthorized, container, false);

        mLoginButton = (Button)v.findViewById(R.id.btn_fb_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mSimpleFacebook.isLogin() && mOnLoginListener != null)
                    mSimpleFacebook.login(mOnLoginListener);
            }
        });

        return v;
    }

    public void setOnLoginListener(SimpleFacebook.OnLoginListener l){
        mOnLoginListener = l;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
    }
}
