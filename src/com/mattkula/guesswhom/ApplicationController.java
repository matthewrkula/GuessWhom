package com.mattkula.guesswhom;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by matt on 2/11/14.
 */
public class ApplicationController extends Application {

    private RequestQueue requestQueue;

    private static ApplicationController instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static synchronized ApplicationController getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue(){

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }
}
