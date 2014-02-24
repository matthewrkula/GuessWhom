package com.mattkula.guesswhom.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by matt on 2/23/14.
 */
public class CustomTextView extends TextView {

    static Typeface typeface;

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
        setTypeface(typeface);
    }
}
