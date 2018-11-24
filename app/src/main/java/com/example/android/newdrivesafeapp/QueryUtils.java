package com.example.android.newdrivesafeapp;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private QueryUtils() {
    }

    public static double[][] getHotspots(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        Log.v(LOG_TAG, "url created");

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        double[][] coordinates = getCoordinates(jsonResponse);

        // Return the list of {@link Earthquake}s
        return coordinates;
    }
    private static double[][] getCoordinates(String JSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(JSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        double[][] coordinates = new double[10][];

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(JSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray firstArray = baseJsonResponse.getJSONArray("points");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < firstArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONArray coordinate = firstArray.getJSONArray(i);
                double lat = coordinate.getDouble(0);
                double lon = coordinate.getDouble(1);
                double[] coor = {lat,lon};
                coordinates[i] = coor;

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Hotspots", "Problem parsing the hotspot JSON results", e);
        }
        Log.v(LOG_TAG, "Coordinates returned");
        // Return the list of earthquakes
        return coordinates;
    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
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
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();
            Log.v(LOG_TAG, "Connection set");

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the hotspot JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }
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
        Log.v(LOG_TAG, "Stream read from");
        return output.toString();
    }
}

