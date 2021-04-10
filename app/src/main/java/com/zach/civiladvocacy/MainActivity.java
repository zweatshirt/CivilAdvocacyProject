package com.zach.civiladvocacy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

/* CS442 Project 4 | Zachery Linscott */

/* Note to self:
* Project gathers data about politicians.
* Links and socials are added for each politician, or 'official'
* Cards were implemented in the RecyclerView, I wanted to try something new.
* Testing offline connectivity with the emulator is really a pain.
* I think if this application were implemented further it could become something
* really interesting. I just think that the Civic Info API really lacks info about
* local officials.
*
* APIs used: Google Civic Information, Ponopto, Universal Image Loader
*
* I think the greatest think I learned from this is to allow cleartext http support
* in Manifest.xml. Literally spent hours trying to resolve issues.
*/

// TODO: Test internet connectivity, emulator makes it really annoying to.

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnLongClickListener {
    private final String TAG = "MainActivity";

    private RecyclerView officialsView;
    private OfficialListAdapter adapter;
    private List<Official> officials = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static String location = "No specified location";
    TextView locationText;
    RecyclerView.LayoutManager layoutManager;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeface = Typeface.createFromAsset(
                this.getAssets(), "fonts/Roboto-Medium.ttf");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Know Your Government");

        // 'Initialize' RecyclerView to display Officials
        officialsView = findViewById(R.id.rView);
        layoutManager = new LinearLayoutManager(this);
        officialsView.setLayoutManager(layoutManager);
        adapter = new OfficialListAdapter(officials, this);
        officialsView.setAdapter(adapter);

        locationText = findViewById(R.id.locationText);
        locationText.setTypeface(typeface);

        mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);

        checkAndDetermineLocation();

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        checkAndDetermineLocation();
//    }

//    @Override
//    public void onRestart() {
//        super.onRestart();
//        checkAndDetermineLocation();
//    }

    /* OPTIONS MENU SETUP START */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // menu options selection
        if (item.getItemId() == R.id.about) {
            Intent appInfoSwitch = new Intent(this, AboutActivity.class);
            startActivity(appInfoSwitch);
        } else if (item.getItemId() == R.id.search) {
            buildAddressDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    /* OPTIONS MENU SETUP END */

    /* LOCATION PERMISSIONS START */

    // check user connection and determines user location
    private void checkAndDetermineLocation() {
        if (isNetworkConnected()) {
            determineLocation();
        }
        else {
            locationText.setText(R.string.noconnection);
            noConnection();
        }
    }

    // hasPermission() checks
    @SuppressLint("MissingPermission")
    private void determineLocation() {
        if (hasPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, loc -> {
                        // Got last known location. In some rare situations this can be null.
                        if (loc != null) {
                            location = getUserLocation(loc);
                            startApiInfoRunnable(location);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(
                            MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    locationText.setText(R.string.deniedText);
                }
            }
        }
    }

    /* LOCATION PERMISSIONS END */

    /* Only used for user's location, NOT searched location */
    private String getUserLocation(Location loc) {
        StringJoiner sj = new StringJoiner(", ");
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            Address address = addresses.get(0);

                if (address.getSubThoroughfare() != null) sj.add(address.getSubThoroughfare());
                if (address.getThoroughfare() != null) sj.add(address.getThoroughfare());
                if (address.getLocality() != null) sj.add(address.getLocality());
                if (address.getAdminArea() != null) sj.add(address.getAdminArea());
                if (address.getPostalCode() != null) sj.add(address.getPostalCode());

            return sj.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /* USER LOCATION SEARCH PARSING START */

    // Build dialog for user to search locations on options menu search selection
    private void buildAddressDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View addressDialog = inflater.inflate(R.layout.search_address_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addressDialog);

        // Grab user's location search request, pass to parseLocSearch()
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText searchView = addressDialog.findViewById(R.id.user_query);
                String locationQuery = searchView.getText().toString();
                parseLocSearch(locationQuery);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });

        builder.show();
    }

    // Parse user query String into valid location address
    private void parseLocSearch(String locationQuery) {
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        TextView locationText = findViewById(R.id.locationText);
        try {
            List<Address> addresses;
            if (locationQuery.trim().isEmpty()) {
                Toast.makeText(this, "Must enter a location.", Toast.LENGTH_LONG).show();
                return;
            }

            addresses = geo.getFromLocationName(locationQuery, 1);
            if (addresses.size() == 0) {
                locationText.setText(R.string.addressNotFound);
                Toast.makeText(this,
                        "Please enter a valid address: [CITY, STATE ABBREVIATION] or [ZIP CODE].", Toast.LENGTH_LONG);
                return;
            }
            Address address = addresses.get(0);
            StringJoiner sj = new StringJoiner(", ");
            if (address.getSubThoroughfare() != null) sj.add(address.getSubThoroughfare());
            if (address.getThoroughfare() != null) sj.add(address.getThoroughfare());
            if (address.getLocality() != null) sj.add(address.getLocality());
            if (address.getAdminArea() != null) sj.add(address.getAdminArea());
            if (address.getPostalCode() != null) sj.add(address.getPostalCode());

            location = sj.toString();
            startApiInfoRunnable(location);
            // pass location to runnable
        } catch (IOException e) {
            locationText.setText(R.string.addressNotFound);
            Toast.makeText(this,
                    "Please enter a valid address: [CITY, STATE ABBREVIATION] or [ZIP CODE].", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /* USER LOCATION SEARCH PARSING END */

    /* RECYCLER VIEW CLICK METHODS */

    private void noConnection() {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.no_connection_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        TextView title = view.findViewById(R.id.noConnTitle);
        TextView desc = view.findViewById(R.id.noConnMsg);
        title.setTypeface(typeface);
        desc.setTypeface(typeface);
        String titleStr = "No Network Connection";
        String descStr = "Data cannot be accessed or loaded without an internet connection";
        title.setText(titleStr);
        desc.setText(descStr);

        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onClick(View v) {
        int pos = officialsView.getChildAdapterPosition(v);
        Official official = officials.get(pos);
        // Pass Official object to OfficialActivity
        Intent officialActivityIntent = new Intent(this, OfficialActivity.class);
        officialActivityIntent.putExtra("Official", official);
        officialActivityIntent.putExtra("Location", location);
        startActivity(officialActivityIntent);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    /* RECYCLER VIEW CLICK METHODS END */

    // check network connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return cap != null && (
                cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
    }

    /* RECYCLER VIEW POPULATION START */

    // start thread of ApiInfoRunnable to find officials near user
    private void startApiInfoRunnable(String location) {
        new Thread(new ApiInfoRunnable(MainActivity.this, location)).start();
    }

    // 'normalizes' location to whatever is in JSON
    public void updateLocation(String normalizedLine1, String normalizedCity,
                               String normalizedState, String normalizedZip) {

        StringJoiner sj = new StringJoiner(", ");
        if (normalizedLine1 != null) sj.add(normalizedLine1);
        if (normalizedCity != null) sj.add(normalizedCity);
        if (normalizedState != null) sj.add(normalizedState);
        if (normalizedZip != null) sj.add(normalizedZip);

        location = sj.toString();
        TextView locationView = findViewById(R.id.locationText);
        locationView.setText(location);
    }

    public void failedOfficialsDownload() {
        Log.d(TAG, "Failure to fetch Officials data");
    }

    public void updateOfficialsList(List<Official> officials) {
        this.officials.clear();
        this.officials.addAll(officials);
        sortOfficials();
        adapter.notifyDataSetChanged();
    }

    private void sortOfficials() {
        officials.sort(new Comparator<Official>() {
            @Override
            public int compare(Official o1, Official o2) {
                return o1.getOfficeIndex() - o2.getOfficeIndex();
            }
        });
    }

    /* RECYCLER VIEW POPULATION END */
}