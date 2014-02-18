package com.mattkula.guesswhom.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Game;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

/**
 * Created by matt on 2/9/14.
 */
public class MyGamesAdapter extends BaseAdapter {

    Game[] games;
    Context c;
    PrettyTime prettyTime = new PrettyTime(new Date(System.currentTimeMillis() + 1000*60*60*6), Locale.US);

    public MyGamesAdapter(Context c, Game[] games) {
        this.c = c;
        this.games = games;
    }

    @Override
    public int getCount() {
        return games.length;
    }

    @Override
    public Object getItem(int i) {
        return games[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        View v = convertview;
        Game game = games[i];
        if(v == null)
            v = View.inflate(c, R.layout.listitem_games, null);

        TextView opponent = (TextView)v.findViewById(R.id.text_opponent);
        TextView time = (TextView)v.findViewById(R.id.text_time_last_update);

        String s = "";
        if(PreferenceManager.getProfileId(c).equals(game.whose_turn))
            s = "Your turn.";
        else
            s = PreferenceManager.getProfileId(c).equals(game.opponent_id) ? game.creator_name + "'s turn" : game.opponent_name + "'s turn.";


        opponent.setText(s);
        time.setText(prettyTime.format(game.updated_at));
        ProfilePictureView picture = (ProfilePictureView)v.findViewById(R.id.game_profile_picture);
        picture.setProfileId(game.opponent_id);

        return v;
    }
}
