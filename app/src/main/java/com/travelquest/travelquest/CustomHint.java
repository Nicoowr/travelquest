package com.travelquest.travelquest;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomHint extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes;
    public String hint_text;


    public CustomHint(Activity a, String hint) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.hint_text = hint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_hint);
        yes = (Button) findViewById(R.id.hint_ok);
        yes.setOnClickListener(this);

        TextView hint = findViewById(R.id.hint_text);
        hint.setText(this.hint_text);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hint_ok:
                dismiss();
                break;
            default:
                break;
        }
    }
}
