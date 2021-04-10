package com.zach.civiladvocacy;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ApiInfoRunnable implements Runnable {
    // Query Format: https://www.googleapis.com/civicinfo/v2/representatives?key=Your-API-Key&address=address
    private final String INITIAL_URI = "https://www.googleapis.com/civicinfo/v2/representatives?";
    private final String KEY = "AIzaSyA1WurKkx_fI9GRiA1NrM1swPK2AU_u7uk";
    private final String TAG = "ApiInfoRunnable";
    private final MainActivity main;
    private final String location;

    public ApiInfoRunnable(MainActivity main, String location) {
        this.main = main;
        this.location = location;
    }

    @Override
    public void run() {
        StringBuilder queryString;
        StringBuilder allJson;

        Uri uri;
        String uriString;
        URL url;
        HttpsURLConnection connection;
        BufferedReader reader;
        InputStream connectionInputStream;

        try {
            queryString = new StringBuilder()
                    .append(INITIAL_URI)
                    .append("key=" + KEY).append("&address=")
                    .append(location);

            uri = Uri.parse(queryString.toString());
            url = new URL(uri.toString());
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.d(TAG, "ApiInfoRunnable: HTTP ResponseCode NOT OK: "
                        + connection.getResponseCode());
                finalResults(null);
                return;
            }
            connectionInputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(connectionInputStream));

            allJson = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                allJson.append(line);
            }

            finalResults(allJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Official> parseJSONForOfficials(String result) {
        List<Official> officialsList = new ArrayList<>();

        try {
            // JSON to extract
            JSONObject allInfo = new JSONObject(result);

            // Normalized address, set location in MainActivity to this
            JSONObject normalizedInput = allInfo.getJSONObject("normalizedInput");
            fetchNormalizedLine(normalizedInput);

            JSONArray offices = allInfo.getJSONArray("offices");
            JSONArray officials = allInfo.getJSONArray("officials");

            // Iterate through offices array
            for (int i = 0; i < offices.length(); i++) {
                JSONObject officesObj = (JSONObject) offices.get(i);

                String officeTitle = officesObj.getString("name");

                // offices array contains indices array officialIndices pointing to 'officials' objects
                JSONArray officialIndices = officesObj.getJSONArray("officialIndices");

                // maintain lowest index of official to ensure ordering in RecyclerView
                int lowestIndex = (int) officialIndices.get(0);

                for (int j = 0; j < officialIndices.length(); j++) {

                    // Official object to populate with JSON data
                    Official officialObject = new Official();

                    officialObject.setOfficeTitle(officeTitle);
                    officialObject.setOfficeIndex(lowestIndex);


                    // point to official by using index stored in officialIndices
                    int index = (int) officialIndices.get(j);
                    JSONObject jsonOfficial = (JSONObject) officials.get(index);

                    // Official's name
                    String name = jsonOfficial.getString("name");
                    officialObject.setName(name);

                    fetchAddressInfo(jsonOfficial, officialObject);

                    // parse for party
                    fetchParty(jsonOfficial, officialObject);

                    // parse for phone
                    fetchPhone(jsonOfficial, officialObject);

                    // parse for url
                    fetchUrl(jsonOfficial, officialObject);

                    // parse for email
                    fetchEmail(jsonOfficial, officialObject);

                    // photoUrl
                    fetchPhotoUrl(jsonOfficial, officialObject);

                    // add Facebook, Twitter, YouTube urls if provided by JSON
                    fetchChannels(jsonOfficial, officialObject);

                    officialsList.add(officialObject);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return officialsList;
    }

    private void fetchChannels(JSONObject jsonOfficial, Official officialObject)
            throws JSONException {
        String facebook = "";
        String twitter = "";
        String youtube = "";
        // play it safe
        if (jsonOfficial.has("channels")) {
            JSONArray channels = jsonOfficial.getJSONArray("channels");
            for (int l = 0; l < channels.length(); l++) {
                JSONObject channel = (JSONObject) channels.get(l);
                if (channel.getString("type").equals("Facebook")) {
                    facebook = channel.getString("id");
                }
                if (channel.getString("type").equals("Twitter")) {
                    twitter = channel.getString("id");
                }
                if (channel.getString("type").equals("YouTube")) {
                    youtube = channel.getString("id");
                }
            }
        }

        if (!facebook.isEmpty()) officialObject.setFacebookId(facebook);
        if (!twitter.isEmpty()) officialObject.setTwitterId(twitter);
        if (!youtube.isEmpty()) officialObject.setYoutubeId(youtube);
    }

    private void fetchPhotoUrl(JSONObject jsonOfficial, Official officialObject) throws JSONException {
        String photoUrl = "";
        if (jsonOfficial.has("photoUrl")) {
            photoUrl = jsonOfficial.getString("photoUrl");
        }

        if (!photoUrl.isEmpty()) officialObject.setPhotoUrl(photoUrl);
    }

    private void fetchEmail(JSONObject jsonOfficial, Official officialObject) throws JSONException {
        String email = "";
        if (jsonOfficial.has("emails")) {
            JSONArray emails = jsonOfficial.getJSONArray("emails");
            if (!(emails.length() == 0)) {
                email = (String) emails.get(0);
            }
        }

        if (!email.isEmpty()) officialObject.setEmail(email);
    }

    private void fetchUrl(JSONObject jsonOfficial, Official officialObject) throws JSONException {
        String url = "";
        if (jsonOfficial.has("urls")) {
            JSONArray urls = jsonOfficial.getJSONArray("urls");
            if (!(urls.length() == 0)) {
                url = (String) urls.get(0);
            }
        }

        if (!url.isEmpty()) officialObject.setWebUrl(url);
    }

    public void fetchPhone(JSONObject jsonOfficial, Official officialObject) throws JSONException {
        String phone = "";
        if (jsonOfficial.has("phones")) {
            JSONArray phones = jsonOfficial.getJSONArray("phones");
            if (!(phones.length() == 0)) {
                phone = (String) phones.get(0);
            }
        }

        if (!phone.isEmpty()) officialObject.setPhone(phone);
    }

    private void fetchParty(JSONObject official, Official officialObject) throws JSONException {
        String party;
        if (official.has("party")) {
            party = official.getString("party");
        } else party = "Unknown";

        if (party.isEmpty()) {
            officialObject.setParty("Unknown");
        } else officialObject.setParty(party);
    }

    private void fetchAddressInfo(JSONObject official, Official officialObject)
            throws JSONException {
        String line = "";
        String city = "";
        String state = "";
        String zip = "";
        if (official.has("address")) {
            JSONArray addresses = official.getJSONArray("address");
            JSONObject address = (JSONObject) addresses.get(0);
            line = buildLine(address);
            city = address.getString("city");
            state = address.getString("state");
            zip = address.getString("zip");
        }
        if (!line.isEmpty()) officialObject.setLine(line);
        if (!city.isEmpty()) officialObject.setCity(city);
        if (!state.isEmpty()) officialObject.setState(state);
        if (!zip.isEmpty()) officialObject.setZip(zip);
    }

    private void fetchNormalizedLine(JSONObject normalizedInput) throws JSONException {
        String normalizedLine1 = null;
        String normalizedCity = null;
        String normalizedState = null;
        String normalizedZip = null;

        try {

            if (!normalizedInput.getString("line1").isEmpty()) {
                normalizedLine1 = normalizedInput.getString("line1");
            }
            if (!normalizedInput.getString("city").isEmpty()) {
                normalizedCity = normalizedInput.getString("city");
            }
            if (!normalizedInput.getString("state").isEmpty()) {
                normalizedState = normalizedInput.getString("state");
            }
            if (!normalizedInput.getString("zip").isEmpty()) {
                normalizedZip = normalizedInput.getString("zip");
            }

        } finally {
            String finalNormalizedLine = normalizedLine1;
            String finalNormalizedCity = normalizedCity;
            String finalNormalizedState = normalizedState;
            String finalNormalizedZip = normalizedZip;
            main.runOnUiThread(() -> {
                main.updateLocation(finalNormalizedLine, finalNormalizedCity, finalNormalizedState, finalNormalizedZip);
            });
        }
    }

    private String buildLine(JSONObject address) throws JSONException {
        StringBuilder line = new StringBuilder();

        if (address.has("line1")) {
            line.append(address.getString("line1"));
        }
        if (address.has("line2")) {
            line.append(address.getString("line2"));
        }
        if (address.has("line3")) {
            line.append(address.getString("line3"));
        }

        return line.toString();
    }

    public void finalResults(String result) {
        if (result == null) {
            Log.d(TAG, "finalResults: FAILURE to download Officials information.");
            main.runOnUiThread(main::failedOfficialsDownload);
        } else {
            // if connection OK, populate stocksList
            final List<Official> officials = parseJSONForOfficials(result);
            main.runOnUiThread(() -> {
                // Log.d(TAG, "Loaded " + officials.size() + " officials.");
                main.updateOfficialsList(officials);
            });
        }
    }

}
