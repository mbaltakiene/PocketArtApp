package com.example.android.pocketartapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.pocketartapp.ui.Painting;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margarita baltakiene on 15/07/2018.
 */

public final class QueryUtils {
    /**
     * Constant value of the painting id key from JSON object
     */
    public static final String PAINTING_ID = "ID";

    /**
     * Constant value of the painting author key from JSON object
     */
    public static final String PAINTING_AUTHOR = "AUTHOR";

    /**
     * Constant value of the painting author years from JSON object
     */
    public static final String PAINTING_AUTHOR_LIVED = "BORN-DIED";

    /**
     * Constant value of the painting title from JSON object
     */
    public static final String PAINTING_TITLE = "TITLE";

    /**
     * Constant value of the painting date from JSON object
     */
    public static final String PAINTING_DATE = "DATE";

    /**
     * Constant value of the painting technique from JSON object
     */
    public static final String PAINTING_TECHNIQUE = "TECHNIQUE";

    /**
     * Constant value of the painting location from JSON object
     */
    public static final String PAINTING_LOCATION = "LOCATION";

    /**
     * Constant value of the painting image url from JSON object
     */
    public static final String PAINTING_URL = "URL";

    /**
     * Constant value of the painting art form from JSON object
     */
    public static final String PAINTING_FORM = "FORM";

    /**
     * Constant value of the painting type from JSON object
     */
    public static final String PAINTING_TYPE = "TYPE";

    /**
     * Constant value of the painting school from JSON object
     */
    public static final String PAINTING_SCHOOL = "SCHOOL";

    /**
     * Constant value of the painting description from JSON object
     */
    public static final String PAINTING_DESCRIPTION = "DESCRIPTION";

    /**
     * Constant value of the painting time frame from JSON object
     */
    public static final String PAINTING_TIMEFRAME = "TIMEFRAME";


    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
        throw new AssertionError("No QueryUtils instances allowed!");
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url is a given URL
     * @return String output of the response
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    Log.e(LOG_TAG, "Service unavailable: " + urlConnection.getResponseCode());
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the paintings JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Reads the input stream of byte code and converts it to string data object
     *
     * @param inputStream stream of byte code
     * @return String object
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * The method checks if device is connected to the Internet.
     *
     * @param context of the Activity
     * @return true is Internet is connected
     */
    public static boolean isConnected(Context context) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static List<Painting> fetchPaintingsData(URL url) {


        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Painting} object
        List<Painting> paintings = extractFeaturesFromJson(jsonResponse);

        // Return the {@link Painting}
        return paintings;
    }

    /**
     * Extract features from JSON object
     *
     * @param paintingsJSON string JSON response from the server
     * @return list of Painting objects
     */
    private static List<Painting> extractFeaturesFromJson(String paintingsJSON) {
        // If the JSON string is empty or null, then return early.

        if (TextUtils.isEmpty(paintingsJSON)) {
            return null;
        }
        List<Painting> paintings = new ArrayList();
        try {
            JSONArray resultsArray = new JSONArray(paintingsJSON);

            for (int i = 0; i < resultsArray.length(); i++) {
                int id = resultsArray.getJSONObject(i).optInt(PAINTING_ID);
                String title = resultsArray.getJSONObject(i).optString(PAINTING_TITLE);
                String author = resultsArray.getJSONObject(i).optString(PAINTING_AUTHOR);
                String bornDied = resultsArray.getJSONObject(i).optString(PAINTING_AUTHOR_LIVED);
                String date = resultsArray.getJSONObject(i).optString(PAINTING_AUTHOR_LIVED);
                String technique = resultsArray.getJSONObject(i).optString(PAINTING_TECHNIQUE);
                String location = resultsArray.getJSONObject(i).optString(PAINTING_LOCATION);
                String url = resultsArray.getJSONObject(i).optString(PAINTING_URL);
                String form = resultsArray.getJSONObject(i).optString(PAINTING_FORM);
                String type = resultsArray.getJSONObject(i).optString(PAINTING_TYPE);
                String school = resultsArray.getJSONObject(i).optString(PAINTING_SCHOOL);
                String description = resultsArray.getJSONObject(i).optString(PAINTING_DESCRIPTION);
                String timeframe = resultsArray.getJSONObject(i).optString(PAINTING_TIMEFRAME);
                paintings.add(new Painting(id, title, author, bornDied, date, technique, location,
                        url, form, type, school, description, timeframe));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the paintings JSON results", e);
        }
        return paintings;
    }


}
