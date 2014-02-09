package com.mattkula.guesswhom.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.inputmethod.InputMethod;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.widget.UserSettingsFragment;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.ui.fragments.AuthorizedMainFragment;
import com.mattkula.guesswhom.ui.fragments.UnauthorizedMainFragment;
import com.sromku.simple.fb.Permissions;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class MainActivity extends FragmentActivity {

    UnauthorizedMainFragment unauthorizedFragment;
    AuthorizedMainFragment authorizedFragment;

    private final int UNAUTHORIZED = 0;
    private final int AUTHORIZED = 1;
    private final int SETTINGS = 2;
    Fragment[] fragments = new Fragment[2];

    private boolean isResumed = false;

//    private UiLifecycleHelper uiHelper;
//    private Session.StatusCallback callback = new Session.StatusCallback(){
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//            onSessionStateChanged(session, state, exception);
//        }
//    };

    SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        uiHelper = new UiLifecycleHelper(this, callback);
//        uiHelper.onCreate(savedInstanceState);

        Permissions[] permissions = new Permissions[]{
                Permissions.BASIC_INFO,
                Permissions.READ_FRIENDLISTS
        };

        SimpleFacebookConfiguration config = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.app_id))
                .setPermissions(permissions)
                .build();

        SimpleFacebook.setConfiguration(config);

        authorizedFragment = (AuthorizedMainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_authorized);
        unauthorizedFragment = (UnauthorizedMainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_unauthorized);
        unauthorizedFragment.setOnLoginListener(loginListener);

        fragments[UNAUTHORIZED] = unauthorizedFragment;
        fragments[AUTHORIZED] = authorizedFragment;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        for(int i=0; i < fragments.length; i++){
            transaction.hide(fragments[i]);
        }
        transaction.commit();
    }

//    private void onSessionStateChanged(Session session, SessionState state, Exception e){
//        if(isResumed){
//            FragmentManager manager = getSupportFragmentManager();
//            int count = manager.getBackStackEntryCount();
//
//            for(int i=0; i < count; i++)
//                manager.popBackStack();
//
//            if(state.isOpened())
//                showFragment(AUTHORIZED, true);
//            else
//                showFragment(UNAUTHORIZED, false);
//        }
//    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
//        Session session = Session.getActiveSession();
//
//        if(session != null && session.isOpened())
//            showFragment(AUTHORIZED, false);
//        else
//            showFragment(UNAUTHORIZED, false);

        if(mSimpleFacebook.isLogin())
            showFragment(AUTHORIZED, false);
        else
            showFragment(UNAUTHORIZED, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
//        uiHelper.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
//        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFragment(int fragmentId, boolean addToBackStack){
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        for(int i=0; i < fragments.length; i++){
            if(i == fragmentId){
                trans.show(fragments[i]);
            } else {
                trans.hide(fragments[i]);
            }
        }
        if(addToBackStack)
            trans.addToBackStack(null);
        trans.commit();
    }

    SimpleFacebook.OnLoginListener loginListener = new SimpleFacebook.OnLoginListener() {
        @Override
        public void onLogin() {
            showFragment(AUTHORIZED, false);
        }

        @Override
        public void onNotAcceptingPermissions() {

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
    };

    SimpleFacebook.OnLogoutListener logoutListener = new SimpleFacebook.OnLogoutListener() {
        @Override
        public void onLogout() {
            showFragment(UNAUTHORIZED, false);
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
    };

}
