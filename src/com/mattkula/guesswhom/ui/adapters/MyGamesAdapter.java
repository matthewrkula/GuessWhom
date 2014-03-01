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
import com.mattkula.guesswhom.ui.CustomTextView;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by matt on 2/9/14.
 */
public class MyGamesAdapter extends BaseAdapter {

    Game[] games;
    Context c;
    PrettyTime prettyTime = new PrettyTime(new Date(System.currentTimeMillis() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)), Locale.US);

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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View v = convertView;
        Game game = games[i];
        if(v == null)
            v = View.inflate(c, R.layout.listitem_games, null);

        CustomTextView textWhoseTurn = (CustomTextView)v.findViewById(R.id.text_whose_turn);
        TextView textTime = (TextView)v.findViewById(R.id.text_time_last_update);

        String s = "";

        if(!game.winner.equals("0"))
            s = PreferenceManager.getProfileId(c).equals(game.winner) ? "You won!" : game.opponent_name + " won!";
        else if(PreferenceManager.getProfileId(c).equals(game.whose_turn))
            s = "Your turn.";
        else
            s = PreferenceManager.getProfileId(c).equals(game.opponent_id) ? game.creator_name + "'s turn" : game.opponent_name + "'s turn.";


        textWhoseTurn.setText(s);
        textWhoseTurn.setBold(true);
        textTime.setText(prettyTime.format(game.updated_at));
        ProfilePictureView picture = (ProfilePictureView)v.findViewById(R.id.game_profile_picture);


        picture.setProfileId(game.opponent_id.equals(PreferenceManager.getProfileId(c)) ? game.creator_id : game.opponent_id);

        if(i % 2 == 0)
            v.setBackgroundColor(0x33ffffff);
        else
            v.setBackgroundColor(0x00ffffff);

        return v;
    }
}
