package com.linjiaxiaohai.sendkeys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.linjiaxiaohai.sendkeys.view.FloatButton;

import java.io.IOException;

/**
 * float button
 *
 */
public class FloatService extends Service {

    private FloatButton floatButton;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private DisplayMetrics displayMetrics;


    public FloatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        floatButton = new FloatButton(this);
        floatButton.setImageResource(R.drawable.ic_launcher);
        floatButton.setOnTouchListener(new FloatButton.OnTouchListener() {
            @Override
            public void onClick(View view) {
                try {
                    Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_HOME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDoubleClick(View view) {
                try {
                    Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_MENU);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        floatButton.show(true);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("FloatService", "onDestroy");
        floatButton.show(false);
    }
}
