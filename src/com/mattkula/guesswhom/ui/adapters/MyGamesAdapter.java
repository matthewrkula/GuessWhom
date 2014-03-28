package com.mattkula.guesswhom.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.data.PreferenceManager;
import com.mattkula.guesswhom.data.models.Game;
import com.mattkula.guesswhom.ui.CustomTextView;
import com.squareup.picasso.Picasso;
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
        Log.e("ASDF", prettyTime.format(new Date(System.currentTimeMillis())));
        Log.e("ASDF", TimeZone.getDefault().getDisplayName());
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

        String myId = PreferenceManager.getProfileId(c);

        if(!game.winner.equals("0"))
            s = (myId.equals(game.winner) ? "You won!" : (myId.equals(game.opponent_id) ? game.creator_name + " won!" : game.opponent_name + " won!"));
        else if(myId.equals(game.whose_turn))
            s = "Your turn.";
        else
            s = myId.equals(game.opponent_id) ? game.creator_name + "'s turn" : game.opponent_name + "'s turn.";

        textWhoseTurn.setText(s);
        textWhoseTurn.setBold(true);
        textTime.setText(prettyTime.format(game.updated_at));
        ImageView picture = (ImageView)v.findViewById(R.id.game_profile_picture);

        Picasso.with(c)
                .load(String.format("https://graph.facebook.com/%s/picture?width=%d&height=%d",
                        game.opponent_id.equals(PreferenceManager.getProfileId(c)) ? game.creator_id : game.opponent_id,
                        60,
                        60))
                .placeholder(R.drawable.default_user)
                .into(picture);

        if(i % 2 == 0)
            v.setBackgroundColor(0x33ffffff);
        else
            v.setBackgroundColor(0x00ffffff);

        return v;
    }
}
