package com.example.android.pocketartapp.ui;

import android.os.AsyncTask;

import com.example.android.pocketartapp.utils.FirebaseUtils;
import com.example.android.pocketartapp.utils.QueryUtils;
import com.google.firebase.database.DatabaseReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class FetchDataTask extends AsyncTask<String, Void, List<Painting>> {

    private AsyncTaskCompleteListener<List<Painting>> mListener;

    public FetchDataTask(AsyncTaskCompleteListener listener) {
        mListener = listener;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Painting> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String url = params[0];
        URL requestUrl = null;
        try {
            requestUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            List<Painting> paintings = QueryUtils
                    .fetchPaintingsData(requestUrl);
            return paintings;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Painting> result) {

        DatabaseReference mDatabase = FirebaseUtils.getDatabase();
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                String key = Integer.toString(i);
                mDatabase.child(key).setValue(result.get(i));
            }
        }

        mListener.onTaskComplete(result);
    }


    public interface AsyncTaskCompleteListener<T> {
        void onTaskComplete(T result);
    }
}
