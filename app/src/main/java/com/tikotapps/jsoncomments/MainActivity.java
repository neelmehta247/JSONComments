package com.tikotapps.jsoncomments;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ListActivity {

    ArrayList<HashMap<String, String>> comments;
    ArrayList<HashMap<String, String>> fullComments;

    private final String url = "http://jsonplaceholder.typicode.com/comments";
    protected final String TAG_POST = "postId";
    protected final String TAG_NAME = "name";
    protected final String TAG_EMAIL = "email";
    protected final String TAG_BODY = "body";
    protected int postId = -1;
    ListView lv;
    Button button;
    EditText editText;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = getListView();
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.comment);
        new GetData().execute();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    setNewAdapter(fullComments);
                } else {
                    postId = Integer.parseInt(editText.getText().toString());
                    ArrayList<HashMap<String, String>> newList = new ArrayList<>();
                    for (int i = (postId - 1) * 5; i < (postId * 5); i++) {
                        newList.add(fullComments.get(i));
                    }
                    setNewAdapter(newList);
                }
            }
        });
    }

    void setNewAdapter(ArrayList<HashMap<String, String>> list) {
        comments.clear();
        for (int i = 0; i < list.size(); i++) {
            comments.add(list.get(i));
        }
        SimpleAdapter sa = (SimpleAdapter) getListAdapter();
        sa.notifyDataSetChanged();
    }

    class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fullComments = new ArrayList<>(comments);
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, comments,
                    R.layout.list_item, new String[]{TAG_NAME, TAG_EMAIL,
                    TAG_BODY, TAG_POST}, new int[]{R.id.name, R.id.email,
                    R.id.commentDisp, R.id.postID});

            setListAdapter(adapter);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            comments = new ArrayList<>();

            ServiceHandler sh = new ServiceHandler();
            String jsonString = sh.makeServiceCall(url, ServiceHandler.GET);
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString(TAG_NAME);
                    String postId = obj.getString(TAG_POST);
                    String email = obj.getString(TAG_EMAIL);
                    String body = obj.getString(TAG_BODY);

                    HashMap<String, String> comment = new HashMap<>();
                    comment.put(TAG_BODY, body);
                    comment.put(TAG_EMAIL, email);
                    comment.put(TAG_POST, postId);
                    comment.put(TAG_NAME, name);

                    comments.add(comment);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
