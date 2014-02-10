package com.mattkula.guesswhom.networking;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mattkula.guesswhom.data.Constants;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.adapters.MyGamesAdapter;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matt on 2/9/14.
 */
public class GameNetworkManager {

    private static AsyncHttpClient httpClient = new AsyncHttpClient();
    private static Game[] games;

    public static Game[] getMyGames(Context c){
        return null;
    }
}
