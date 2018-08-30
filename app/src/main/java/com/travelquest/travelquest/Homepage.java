package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class Homepage extends AppCompatActivity {

    Button start_button;
    ProgressBar loading;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        loading = (ProgressBar) findViewById(R.id.loading);
        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading.setVisibility(View.VISIBLE);

                Intent intent = new Intent(Homepage.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
