package com.linjiaxiaohai.sendkeys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.linjiaxiaohai.sendkeys.utils.FloatKeeper;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat mFloatSwitch;
    private SwitchCompat mFloatTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatSwitch = (SwitchCompat) findViewById(R.id.open_float_button);
        mFloatTop = (SwitchCompat) findViewById(R.id.float_top);

        mFloatTop.setChecked(FloatKeeper.isFloatTop(this));

        mFloatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                showFloatButton(b);
            }
        });

        mFloatTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFloatTop(isChecked);
            }
        });
    }

    private void showFloatButton(boolean isFloat) {
        Intent intent = new Intent(this, FloatService.class);
        intent.setAction(isFloat ? FloatService.ACTION_SHOW : FloatService.ACTION_HIDE);
        startService(intent);
    }

    private void setFloatTop(boolean isTop) {
        FloatKeeper.setFloatTop(this, isTop);
        Intent intent = new Intent(this, FloatService.class);
        intent.setAction(FloatService.ACTION_UPDATE);
        startService(intent);
    }
}
