package com.linjiaxiaohai.sendkeys;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;

import com.linjiaxiaohai.sendkeys.view.FloatButton;

import java.io.IOException;

/**
 * float button
 *
 */
public class FloatService extends Service {

    private FloatButton floatButton;
    private boolean isLock;//是否锁屏

    public static final String ACTION_SHOW = "com.linjiaxiaohai.sendkeys.action.show";
    public static final String ACTION_HIDE = "om.linjiaxiaohai.sendkeys.action.hide";
    public static final String ACTION_LOCK = "om.linjiaxiaohai.sendkeys.action.lock";

    public FloatService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_SHOW:
                    show();
                    break;
                case ACTION_HIDE:
                    hide();
                    break;
                case ACTION_LOCK:

                    break;
            }
        }

        return START_NOT_STICKY;
    }

    private void show() {
        if (floatButton == null) {
            floatButton = new FloatButton(this);
            floatButton.setImageResource(R.drawable.ic_touch);
            floatButton.setOnTouchListener(new FloatButton.OnTouchListener() {
                @Override
                public void onClick(View view) {
                    operate("home");
                }

                @Override
                public void onDoubleClick(View view) {
                    operate("lock");
                }
            });

        }
        floatButton.show(true);
        registerScreenReceiver();
    }

    private void hide() {
        floatButton.show(false);
        unregisterReceiver(screenReceiver);
    }

    private void registerScreenReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, filter);
    }

    BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:
                    isLock = true;
                    break;
                case Intent.ACTION_USER_PRESENT:
                    isLock = false;
                    break;
            }
        }
    };

    private void operate(String operate) {
        if (isLock) {
            lock();
        } else {
            try {
                switch (FloatButton.Operate.valueOf(operate.toUpperCase())) {
                    case BACK:
                        goBack();
                        break;
                    case HOME:
                        goToHome();
                        break;
                    case LOCK:
                        lock();
                        break;
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void goBack() {
        try {
            Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToHome() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
    }

    private void lock() {
        Intent intent = new Intent(this, LockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (floatButton.isFloat()) {
            floatButton.update();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        floatButton.show(false);
    }
}
