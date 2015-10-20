package com.linjiaxiaohai.sendkeys.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * Created by mengxn on 15-10-20.
 */
public class FloatButton extends ImageButton {

    private final int DEFAULT_LOCATION_Y = 200;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private DisplayMetrics displayMetrics;

    public FloatButton(Context context) {
        this(context, null);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = getResources().getDisplayMetrics();
        params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.y = 0;
        params.x = 0;

        setBackgroundResource(0);
    }

    public void show(boolean isFloat) {
        try {
            if (isFloat) {
                windowManager.addView(this, params);
            } else {
                windowManager.removeView(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                move(event.getRawX(), event.getRawY());
                return true;
            case MotionEvent.ACTION_UP:
                if (event.getRawX() > displayMetrics.widthPixels / 2) {
                    move(displayMetrics.widthPixels - getWidth() / 2, event.getRawY());
                } else {
                    move(getWidth() / 2, event.getRawY());
                }

                return true;
        }
        return super.onTouchEvent(event);
    }

    private void move(float x, float y) {
        params.x = (int) (x - getWidth()/2);
        params.y = (int) (y - getHeight());
        windowManager.updateViewLayout(this, params);
    }
}
