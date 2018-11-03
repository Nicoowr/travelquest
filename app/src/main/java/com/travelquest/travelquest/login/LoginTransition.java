package com.travelquest.travelquest.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.travelquest.travelquest.Homepage;
import com.travelquest.travelquest.R;

public class LoginTransition extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button next_button;
    String first_name;
    TextView greetings;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_transition);

        // Greet user
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        first_name = pref.getString("first_name", null);
        greetings = (TextView) findViewById(R.id.greetings);
        greetings.setText("Welcome " + first_name);
        greetings.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));


        ImageView user_picture = (ImageView) findViewById(R.id.profilePic);

        // Next button
        next_button = (Button) findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginTransition.this, Homepage.class);
                startActivity(intent);
            }
        });


    }
}
