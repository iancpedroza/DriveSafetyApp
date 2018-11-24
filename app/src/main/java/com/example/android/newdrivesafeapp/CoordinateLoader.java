package com.example.android.newdrivesafeapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class CoordinateLoader extends AsyncTaskLoader<double[][]> {
    /** Tag for log messages */
    private static final String LOG_TAG = CoordinateLoader.class.getName();

    /** Query URL */
    private String mUrl;
    public CoordinateLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public double[][] loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        double[][] hotspots = QueryUtils.getHotspots(mUrl);
        return hotspots;
    }
}
