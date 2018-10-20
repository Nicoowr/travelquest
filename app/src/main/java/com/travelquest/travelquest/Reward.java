package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.travelquest.travelquest.database_handler.PoI;
import com.travelquest.travelquest.login.LoginTransition;

public class Reward extends AppCompatActivity{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button close;
    PoI poi;
    TextView poiCongratulations;
    TextView poiTitle;
    TextView poiDescription;
    ImageView picture;

    Intent intent_from_map;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reward);

        // Greet user
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        // Get PoI
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            poi = (PoI) getIntent().getSerializableExtra("poi");
        }

        poiCongratulations = (TextView) findViewById(R.id.poiCongratulations);
        poiCongratulations.setText("Congratulations " + pref.getString("first_name", null) + "!");
        poiCongratulations.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        poiTitle = (TextView) findViewById(R.id.poiTitle);
        poiTitle.setText("You found the " +  poi.getTitle() + "!");
        poiTitle.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        poiDescription = (TextView) findViewById(R.id.poiDescription);
        poiDescription.setText(poi.getDescription());


        picture = (ImageView) findViewById(R.id.poiPicture);
        Log.d("debug_reward", poi.getImageLink());
        Picasso.with(Reward.this).load(poi.getImageLink()).into(picture);
        //ImageLoadTask imageLoadTask = new ImageLoadTask(poi.getImageLink(), picture);

        // Next button
        close = (Button) findViewById(R.id.next_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Reward.this, Homepage.class);
                //startActivity(intent);
                finish();
            }
        });


    }

}
