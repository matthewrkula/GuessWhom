package com.mattkula.guesswhom.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.ui.adapters.FriendListAdapter;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;

import java.util.Collections;
import java.util.List;

/**
 * Created by matt on 2/9/14.
 */
public class FriendPickerActivity extends Activity {

    public static final String FRIEND_PICKER_EXTRA_ID = "id";
    public static final String FRIEND_PICKER_EXTRA_NAME = "name";

    SimpleFacebook simpleFacebook;

    EditText friendFilterEdit;
    ListView friendList;
    List<Profile> mFriends;

    FriendListAdapter mAdapter;

    String selectedId= "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendpicker);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        friendList = (ListView)findViewById(R.id.list_friend_picker);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(FRIEND_PICKER_EXTRA_ID, ((Profile)friendList.getAdapter().getItem(i)).getId());
                intent.putExtra(FRIEND_PICKER_EXTRA_NAME, ((Profile)friendList.getAdapter().getItem(i)).getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        friendFilterEdit = (EditText)findViewById(R.id.edit_friend_filter);
        friendFilterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(mAdapter != null)
                    mAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance();
        simpleFacebook.getFriends(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    SimpleFacebook.OnFriendsRequestListener listener = new SimpleFacebook.OnFriendsRequestListener() {
        ProgressDialog dialog;

        @Override
        public void onComplete(List<Profile> friends) {
            mFriends = friends;
            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    Collections.sort(mFriends);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(dialog != null && dialog.isShowing())
                        dialog.dismiss();

                    mAdapter = new FriendListAdapter(mFriends, FriendPickerActivity.this);
                    friendList.setAdapter(mAdapter);
                }
            }.execute();
        }

        private void completed(){

        }

        @Override
        public void onThinking() {
            dialog = new ProgressDialog(FriendPickerActivity.this);
            dialog.setTitle("Loading friends");
            dialog.show();
        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String reason) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
