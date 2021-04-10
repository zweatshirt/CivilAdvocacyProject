package com.zach.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {
    TextView detailPositionView;
    TextView detailNameView;
    TextView detailLocationView;
    ImageView detailPoliticianImg;
    ImageView detailPartyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        detailPositionView = findViewById(R.id.detailPositionView);
        detailNameView = findViewById(R.id.detailNameView);
        detailPoliticianImg = findViewById(R.id.detailPoliticianImg);
        detailPartyImg = findViewById(R.id.detailPartyImg);
        detailLocationView = findViewById(R.id.detailLocationView);

        Intent photoIntent = getIntent();
        unpackIntent(photoIntent);
    }

    private void unpackIntent(Intent intent) {
        String url = intent.getStringExtra("PhotoUrl");
        String name = intent.getStringExtra("Name");
        String party = intent.getStringExtra("Party");
        String position = intent.getStringExtra("Position");
        String location = intent.getStringExtra("Location");
        detailNameView.setText(name);
        detailPositionView.setText(position);
        detailLocationView.setText(location);

        usePicasso(url);
        setBackgroundColor(party);
        setParty(party);

    }

    private void setParty(String party) {
        if (party.equals("Republican Party")) {
            String repImgName = "rep_logo";
            int resID = getResources().getIdentifier(
                    repImgName , "drawable", getPackageName());
            detailPartyImg.setImageResource(resID);
        }
        if (party.equals("Nonpartisan")
                || party.equals("Unknown")) {
            detailPartyImg.setVisibility(View.GONE);
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

    private void usePicasso(String url) {
        Picasso.get()
                .load(url)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(detailPoliticianImg, new Callback() {
                    @Override
                    public void onSuccess() {
                        // do nothing
                    }

                    @Override
                    public void onError(Exception e) {
                        ColorDrawable background = (ColorDrawable) getWindow().getDecorView().getBackground();
                        int colorId = background.getColor();
                        if (colorId == Color.BLACK) {
                            detailPoliticianImg.setColorFilter(ContextCompat.getColor(
                                    PhotoDetailActivity.this, android.R.color.white),
                                    PorterDuff.Mode.MULTIPLY);
                        }
                    }
                });

    }
}