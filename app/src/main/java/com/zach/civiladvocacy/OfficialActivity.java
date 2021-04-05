package com.zach.civiladvocacy;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.StringJoiner;

public class OfficialActivity extends AppCompatActivity {
    TextView locationView;
    TextView nameView;
    TextView positionView;
    ImageView politicianImg;
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
    private boolean hasLoaded;
    private String location;
    ImageLoader imageLoader;

    // implement landscape view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs()
                .build();
        imageLoader.init(config);
        nameView = findViewById(R.id.nameView);
        partyText = findViewById(R.id.partyView);
        positionView = findViewById(R.id.positionView);
        politicianImg = findViewById(R.id.detailPoliticianImg);
        locationView = findViewById(R.id.detailLocationView);
        addressLink = findViewById(R.id.addressLink);
        addressView = findViewById(R.id.addressView);
        phoneView = findViewById(R.id.phoneView);
        phoneLink = findViewById(R.id.phoneLink);
        emailView = findViewById(R.id.emailView);
        emailLink = findViewById(R.id.emailLink);
        websiteView = findViewById(R.id.websiteView);
        websiteLink = findViewById(R.id.websiteLink);

        partyImg = findViewById(R.id.detailPartyImg);
        facebookView = findViewById(R.id.facebookImg);
        twitterView = findViewById(R.id.twitterImg);
        youtubeView = findViewById(R.id.youtubeImg);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        setFonts(typeface);

        Intent officialIntent = getIntent();
        unpackIntent(officialIntent);
    }


    private void unpackIntent(Intent intent) {
        if (intent.hasExtra("Official")) {
            official = (Official) intent.getSerializableExtra("Official");
            if (official != null) {
                setAddress(official);
                positionView.setText(official.getOfficeTitle());
                nameView.setText(official.getName());
                setLinks(official);
                setSocials(official);
                setParty(official.getParty());
                setBackgroundColor(official.getParty());
                setPoliticianImg(official.getPhotoUrl());
            }
        }
        if (intent.hasExtra("Location")) {
            locationView.setText(intent.getStringExtra("Location"));
        }
    }

    private void setFonts(Typeface typeface) {
        nameView.setTypeface(typeface);
        partyText.setTypeface(typeface);
        positionView.setTypeface(typeface);
        locationView.setTypeface(typeface);
        addressLink.setTypeface(typeface);
        addressView.setTypeface(typeface);
        emailView.setTypeface(typeface);
        phoneLink.setTypeface(typeface);
        emailLink.setTypeface(typeface);
        websiteView.setTypeface(typeface);
        websiteLink.setTypeface(typeface);
        phoneView.setTypeface(typeface);
    }

    private void setPoliticianImg(String url) {
        if (url != null) {
            Picasso.get().setLoggingEnabled(true);
            Picasso.get()
                    .load(url)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(politicianImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            hasLoaded = true;
                        }

                        // if image load failed from Picasso, try ImageLoader
                        @Override
                        public void onError(Exception e) {
                            tryImageLoader(url);
                        }
                    });
        } else politicianImg.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.missing));
    }

    // if black background, change brokenimage to white, not sure if this actually works
    private void setFailureImgWhite() {
        ColorDrawable background = (ColorDrawable) getWindow().getDecorView().getBackground();
        int colorId = background.getColor();
        if (colorId == Color.BLACK) {
            politicianImg.setColorFilter(ContextCompat.getColor(
                    OfficialActivity.this, android.R.color.white),
                    PorterDuff.Mode.MULTIPLY);
        }
    }

    // If Picasso fails, try Universal Image Loader API
    // This might actually crash the app so may remove
    private void tryImageLoader(String url) {
        imageLoader.displayImage(url, politicianImg, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                politicianImg.setImageDrawable(ContextCompat.getDrawable(
                        OfficialActivity.this, R.drawable.placeholder));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                politicianImg.setImageDrawable(ContextCompat.getDrawable(
                        OfficialActivity.this, R.drawable.brokenimage));
                setFailureImgWhite();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                hasLoaded = true;
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                hasLoaded = false;
            }
        });

    }


    private void setBackgroundColor(String party) {
        if (party.equals("Democratic Party")) {
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        } else if (party.equals("Republican Party")) {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
        } else getWindow().getDecorView().setBackgroundColor(Color.BLACK);
    }

    private void setParty(String party) {
        String pStr = String.format("(%s)", party);
        partyText.setText(pStr);
        if (party.equals("Republican Party")) {
            String repImgName = "rep_logo";
            int resID = getResources().getIdentifier(
                    repImgName, "drawable", getPackageName());
            partyImg.setImageResource(resID);
        }
        if (party.equals("Nonpartisan")
                || party.equals("Unknown")) {
            partyImg.setVisibility(View.GONE);
        }
    }

    private void setAddress(Official official) {
        StringJoiner sj = new StringJoiner(", ");
        if (official.getLine() != null) sj.add(official.getLine());
        if (official.getCity() != null) sj.add(official.getCity());
        if (official.getState() != null) sj.add(official.getState());
        if (official.getZip() != null) sj.add(official.getZip());

        String address = sj.toString();
        if (address != null && !address.isEmpty())
            addressLink.setText(address);
        else {
            addressLink.setVisibility(View.GONE);
            addressView.setVisibility(View.GONE);
        }
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
        } else fb = official.getFacebookId();

        if (official.getTwitterId() == null) {
            twitterView.setVisibility(View.GONE);
        } else twitter = official.getTwitterId();

        if (official.getYoutubeId() == null) {
            youtubeView.setVisibility(View.GONE);
        } else youtube = official.getFacebookId();
    }

    // Politician's image clicked, if successful load -> open PhotoDetail activity
    public void imgClicked(View v) {
        if (hasLoaded) {
            Intent photoDetailIntent = new Intent(this, PhotoDetailActivity.class);
            photoDetailIntent.putExtra("PhotoUrl", official.getPhotoUrl());
            photoDetailIntent.putExtra("Name", official.getName());
            photoDetailIntent.putExtra("Party", official.getParty());
            photoDetailIntent.putExtra("Position", official.getOfficeTitle());
            photoDetailIntent.putExtra("Location", location);
            startActivity(photoDetailIntent);
        }
    }

    public void youtubeClicked(View v) {
        String name = youtube; // replace with data provided from download
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));

        }
    }

    public void facebookClicked(View v) {
        String name = fb; // get name from data downloaded
        String facebook_URL = "https://www.facebook.com/" + name;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            long versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).getLongVersionCode();
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