package com.example.android.newdrivesafeapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.app.LoaderManager.LoaderCallbacks;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<double[][]>{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL =
            "https://curly-dodo-40.localtunnel.me/hotspots/";
    private String FINAL_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                getData(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    public void getData(Location location){
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String tag = lat + "/" + lon;
        FINAL_URL = BASE_URL + tag;
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);

    }
    // Register the listener with the Location Manager to receive location updates
    public void setHotspots(double[][] coordinates){
        String text = "";
        for (int i = 0; i<coordinates.length; i++){
            double lat = coordinates[i][0];
            double lon = coordinates[i][1];
            text += "(" + lat + "," + lon + ")";
            TextView message = (TextView) findViewById(R.id.message);
            message.setText(text);
        }
    }
    @Override
    public Loader<double[][]> onCreateLoader(int i, Bundle bundle) {
        return new CoordinateLoader(this, FINAL_URL);
    }
    @Override
    public void onLoadFinished(Loader<double[][]> loader, double[][] coordinates) {
        // Hide loading indicator because the data has been loaded
        setHotspots(coordinates);
    }
    @Override
    public void onLoaderReset(Loader<double[][]> loader) {
        // Loader reset, so we can clear out our existing data.

    }
}
