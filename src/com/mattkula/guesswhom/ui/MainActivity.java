package com.mattkula.guesswhom.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.PreferenceManager;
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
    Fragment[] fragments = new Fragment[2];

    private boolean isResumed = false;

    SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up SimpleFacebookConfiguration
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
        for(int i=0; i < fragments.length; i++)
            transaction.hide(fragments[i]);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_fb_logout:
                mSimpleFacebook.logout(logoutListener);
                return false;
            case R.id.menu_refresh:
                //TODO refresh page
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if(mSimpleFacebook.isLogin())
            showFragment(AUTHORIZED, false);
        else
            showFragment(UNAUTHORIZED, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
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
        public void onNotAcceptingPermissions() {}

        @Override
        public void onThinking() {}

        @Override
        public void onException(Throwable throwable) {
            Log.e("ASDf", throwable.getMessage());
        }

        @Override
        public void onFail(String reason) {
            Log.e("ASDF", reason);
        }
    };

    SimpleFacebook.OnLogoutListener logoutListener = new SimpleFacebook.OnLogoutListener() {
        @Override
        public void onLogout() {
            showFragment(UNAUTHORIZED, false);
            PreferenceManager.logout(MainActivity.this);
        }

        @Override
        public void onThinking() {}

        @Override
        public void onException(Throwable throwable) {}

        @Override
        public void onFail(String reason) {}
    };

}
