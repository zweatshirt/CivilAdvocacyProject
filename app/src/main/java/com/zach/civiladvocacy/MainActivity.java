package com.zach.civiladvocacy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

// Go to office hours about setting up ic_launcher

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnLongClickListener {

    private RecyclerView officialsView;
    private OfficialListAdapter adapter;
    private List<Official> officials = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static String location = "No specified location";
    TextView locationText;
    RecyclerView.LayoutManager layoutManager;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Know Your Government");
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        officialsView = findViewById(R.id.rView);
        layoutManager = new LinearLayoutManager(this);
        officialsView.setLayoutManager(layoutManager);
        adapter = new OfficialListAdapter(officials, this);
        officialsView.setAdapter(adapter);

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();
        locationText = findViewById(R.id.locationText);
        locationText.setTypeface(typeface);
    }


    /* OPTIONS MENU SETUP START */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        // change icon colors to white
        // change menu title


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
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
    @RequiresApi(api = Build.VERSION_CODES.N)
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
            @RequiresApi(api = Build.VERSION_CODES.N) // Because of StringJoiner
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
    @RequiresApi(api = Build.VERSION_CODES.N) // Because of StringJoiner
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


    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void startApiInfoRunnable(String location) {
        new Thread(new ApiInfoRunnable(MainActivity.this, location)).start();
    }

    // I don't really get the point of doing this but the project doc said to do it so..
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLocation(String normalizedLine1, String normalizedCity, String normalizedState, String normalizedZip) {
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

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateOfficialsList(List<Official> officials) {
        this.officials.clear();
        this.officials.addAll(officials);
        sortOfficials();
        adapter.notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortOfficials() {
        officials.sort(new Comparator<Official>() {
            @Override
            public int compare(Official o1, Official o2) {
                return o1.getOfficeIndex() - o2.getOfficeIndex();
            }
        });
    }
}