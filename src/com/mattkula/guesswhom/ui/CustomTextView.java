package com.mattkula.guesswhom.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by matt on 2/23/14.
 */
public class CustomTextView extends TextView {

    static Typeface typeface;
    static Typeface typefaceBold;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if(typeface == null){
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
        }
        if(typefaceBold == null){
            typefaceBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Bold.ttf");
        }
        setTypeface(typeface);
    }

    public void setBold(boolean setBold){
        if(setBold){
            setTypeface(typefaceBold);
        } else {
            setTypeface(typeface);
        }
    }
}
