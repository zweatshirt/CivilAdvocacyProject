package com.zach.civiladvocacy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.StringJoiner;

public class OfficialActivity extends AppCompatActivity {
    TextView locationView;
    TextView nameView;
    TextView positionView;
    TextView partyText;
    TextView addressView;
    TextView addressLink;
    TextView phoneView;
    TextView phoneLink;
    TextView emailView;
    TextView emailLink;
    TextView websiteView;
    TextView websiteLink;
    ImageView partyImg;
    ImageView facebookView;
    ImageView twitterView;
    ImageView youtubeView;
    private String fb;
    private String twitter;
    private String youtube;
    private Official official;
    // implement landscape view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        final Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto);
        nameView = findViewById(R.id.nameView);
        nameView.setTypeface(typeface);
        partyText = findViewById(R.id.partyView);
        partyText.setTypeface(typeface);
        positionView = findViewById(R.id.positionView);
        positionView.setTypeface(typeface);
        locationView = findViewById(R.id.officialLocationView);
        locationView.setTypeface(typeface);
        addressLink = findViewById(R.id.addressLink);
        addressLink.setTypeface(typeface);
        addressView = findViewById(R.id.addressView);
        addressView.setTypeface(typeface);
        phoneLink = findViewById(R.id.phoneLink);
        phoneLink.setTypeface(typeface);
        emailView = findViewById(R.id.emailView);
        emailView.setTypeface(typeface);
        emailLink = findViewById(R.id.emailLink);
        emailLink.setTypeface(typeface);
        websiteView = findViewById(R.id.websiteView);
        websiteView.setTypeface(typeface);
        websiteLink = findViewById(R.id.websiteLink);
        websiteView.setTypeface(typeface);
        partyImg = findViewById(R.id.partyImg);;
        facebookView = findViewById(R.id.facebookImg);
        twitterView = findViewById(R.id.twitterImg);
        youtubeView = findViewById(R.id.youtubeImg);

        Intent officialIntent = getIntent();
        unpackIntent(officialIntent);
    }

    private void unpackIntent(Intent intent) {
        if (intent.hasExtra("Official")) {
            official = (Official) intent.getSerializableExtra("Official");
            if (official != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setAddress(official);
                }
                positionView.setText(official.getOfficeTitle());
                nameView.setText(official.getName());
                setLinks(official);
                setSocials(official);
                setParty(official.getParty());
                setBackgroundColor(official.getParty());
            }
        }
        if (intent.hasExtra("Location")) {
            locationView.setText(intent.getStringExtra("Location"));
        }
    }

    private void setBackgroundColor(String party) {
        if (party.equals("Democratic Party")) {
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        }
        else if (party.equals("Republican Party")) {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        }
        else getWindow().getDecorView().setBackgroundColor(Color.BLACK);

    }

    private void setParty(String party) {
        String pStr = String.format("(%s)", party);
        partyText.setText(pStr);
        if (party.equals("Republican Party")) {
            String repImgName = "rep_logo";
            int resID = getResources().getIdentifier(repImgName , "drawable", getPackageName());
            partyImg.setImageResource(resID);
        }
        if (party.equals("Nonpartisan")
                || party.equals("Unknown")) {
            partyImg.setVisibility(View.GONE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAddress(Official official) {
        StringJoiner sj = new StringJoiner(", ");
        if (official.getLine() != null) sj.add(official.getLine());
        if (official.getCity() != null) sj.add(official.getCity());
        if (official.getState() != null) sj.add(official.getState());
        if (official.getZip() != null) sj.add(official.getZip());

        addressLink.setText(sj.toString());
        Linkify.addLinks(addressLink, Linkify.ALL);
    }

    private void setLinks(Official official) {
        if (official.getPhone() == null) {
            phoneLink.setVisibility(View.GONE);
            phoneView.setVisibility(View.GONE);
        } else phoneLink.setText(official.getPhone());
        if (official.getEmail() == null) {
            emailLink.setVisibility(View.GONE);
            emailView.setVisibility(View.GONE);
        } else emailLink.setText(official.getEmail());
        if (official.getWebUrl() == null) {
            websiteView.setVisibility(View.GONE);
            websiteLink.setVisibility(View.GONE);
        } else websiteLink.setText(official.getWebUrl());
        Linkify.addLinks(phoneLink, Linkify.ALL);
        Linkify.addLinks(emailLink, Linkify.ALL);
        Linkify.addLinks(websiteLink, Linkify.ALL);
    }

    private void setSocials(Official official) {
        if (official.getFacebookId() == null) {
            facebookView.setVisibility(View.GONE);
        }
        else fb = official.getFacebookId();

        if (official.getTwitterId() == null) {
            twitterView.setVisibility(View.GONE);
        }
        else twitter = official.getTwitterId();

        if (official.getYoutubeId() == null) {
            youtubeView.setVisibility(View.GONE);
        }
        else youtube = official.getFacebookId();
    }
    // implement implicit intents for email, phone, address (geo)
    // implement methods to hide social media imgs if DNE

    public void youtubeClicked(View v) {
        String name = youtube; // replace with data provided from download
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));

        }
    }

    public void facebookClicked(View v) {
        String name = fb; // get name from data downloaded
        String facebook_URL = "https://www.facebook.com/" + name; // replace with data provided from download
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + facebook_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + fb;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = facebook_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        String name = twitter; // replace with data provided from download
        Intent intent = null;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);

    }



}