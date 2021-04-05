package com.zach.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private TextView linkToApi;
    private TextView appTitleView;
    private TextView providedView;
    private TextView Zach;
    private TextView copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Typeface typeface = Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-Medium.ttf");

        linkToApi = findViewById(R.id.apiLinkView);
        appTitleView = findViewById(R.id.appTitleView);
        providedView = findViewById(R.id.providedView);
        Zach = findViewById(R.id.Zach);
        copyright = findViewById(R.id.copyright);
        linkToApi.setTypeface(typeface);
        appTitleView.setTypeface(typeface);
        providedView.setTypeface(typeface);
        Zach.setTypeface(typeface);
    }

    public void goToAPILink(View v) {
        String url = "https://developers.google.com/civic-information/";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}