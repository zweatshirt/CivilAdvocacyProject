package com.zach.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private TextView linkToApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        linkToApi = findViewById(R.id.apiLinkView);
    }

    public void goToAPILink(View v) {
        String url = "https://developers.google.com/civic-information/";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}