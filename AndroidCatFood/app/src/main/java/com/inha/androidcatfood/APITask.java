package com.inha.androidcatfood;

import android.os.AsyncTask;

import org.json.JSONObject;

public class APITask extends AsyncTask<String, String, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... strings) {
        APIClient client = new APIClient();
        JSONObject obj = client.reqAPI();

        return obj;
    }
}
