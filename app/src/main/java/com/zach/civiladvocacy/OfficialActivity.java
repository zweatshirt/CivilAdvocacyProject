package com.zach.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OfficialActivity extends AppCompatActivity {
    TextView locationView;
    TextView addressView;
    TextView phoneView;
    TextView emailView;
    TextView websiteView;
    ImageView facebookView;
    ImageView twitterView;
    ImageView youtubeView;

    // implement landscape view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        TextView locationView = findViewById(R.id.officialLocationView);
        TextView addressLink = findViewById(R.id.addressLink);
        TextView phoneLink = findViewById(R.id.phoneLink);
        TextView emailLink = findViewById(R.id.emailLink);
        TextView websiteLink = findViewById(R.id.websiteLink);

        // find way to 'Linkify'
        // implement hide if no link
        ImageView facebookView = findViewById(R.id.facebookImg);
        ImageView twitterView = findViewById(R.id.twitterImg);
        ImageView youtubeView = findViewById(R.id.youtubeImg);

        // implement hide if false
        boolean addressBool = Linkify.addLinks(addressLink, Linkify.ALL);
        boolean phoneBool = Linkify.addLinks(phoneLink, Linkify.ALL);
        boolean emailBool = Linkify.addLinks(emailLink, Linkify.ALL);
        boolean websiteBool = Linkify.addLinks(websiteLink, Linkify.ALL);

    }

    // implement implicit intents for email, phone, address (geo)
    // implement methods to hide social media imgs if DNE

    public void youtubeClicked(View v) {
        String name = ""; // replace with data provided from download
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
        String name = ""; // get name from data downloaded
        String facebook_URL = "https://www.facebook.com/" + name; // replace with data provided from download
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + facebook_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/";
//                urlToUse = "fb://page/" + channels.get("Facebook");
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = facebook_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        String name = ""; // replace with data provided from download
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