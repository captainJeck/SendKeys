package com.linjiaxiaohai.sendkeys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.linjiaxiaohai.sendkeys.view.FloatButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat mFloatSwitch;
    private FloatButton floatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatSwitch = (SwitchCompat) findViewById(R.id.open_float_button);

        mFloatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                floatButton.show(b);
            }
        });

        floatButton = new FloatButton(this);
        floatButton.setImageResource(R.drawable.ic_launcher);
        floatButton.setOnClickListener(floatClickListener);
    }

    View.OnClickListener floatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_BACK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
