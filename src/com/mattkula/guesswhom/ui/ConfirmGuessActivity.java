package com.mattkula.guesswhom.ui;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.mattkula.guesswhom.R;
import com.squareup.picasso.Picasso;

/**
 * Created by matt on 2/18/14.
 */
public class ConfirmGuessActivity extends Activity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_NAME = "name";

    ImageView profilePicture;
    TextView confirmNameText;
    Button confirmYesBtn;
    Button confirmNoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmguess);

        profilePicture = (ImageView)findViewById(R.id.image_confirm_profile);

        confirmNameText = (TextView)findViewById(R.id.text_confirm_name);
        confirmYesBtn = (Button)findViewById(R.id.btn_confirm_yes);
        confirmYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirmNoBtn = (Button)findViewById(R.id.btn_confirm_no);
        confirmNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras(); String url = extras.getString(EXTRA_URL);
        String name = extras.getString(EXTRA_NAME);

        confirmNameText.setText("Do you want to guess " + name + "?");
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
