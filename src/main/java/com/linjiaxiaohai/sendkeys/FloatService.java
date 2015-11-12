package com.linjiaxiaohai.sendkeys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import com.linjiaxiaohai.sendkeys.view.FloatButton;

/**
 * float button
 *
 */
public class FloatService extends Service {

    private FloatButton floatButton;

    public static final String ACTION_SHOW = "com.linjiaxiaohai.sendkeys.action.show";
    public static final String ACTION_HIDE = "om.linjiaxiaohai.sendkeys.action.hide";

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
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(homeIntent);
                }

                @Override
                public void onDoubleClick(View view) {
                }
            });

        }
        floatButton.show(true);
    }

    private void hide() {
        floatButton.show(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        floatButton.show(false);
    }
}
