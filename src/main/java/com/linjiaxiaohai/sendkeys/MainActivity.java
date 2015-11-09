package com.linjiaxiaohai.sendkeys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat mFloatSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatSwitch = (SwitchCompat) findViewById(R.id.open_float_button);

        mFloatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                showFloatButton(b);
            }
        });
    }

    private void showFloatButton(boolean isFloat) {
        Intent intent = new Intent(this, FloatService.class);
        intent.setAction(isFloat ? FloatService.ACTION_SHOW : FloatService.ACTION_HIDE);
        startService(intent);
    }
}
