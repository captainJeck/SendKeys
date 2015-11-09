package com.linjiaxiaohai.sendkeys.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.Locale;

/**
 * Created by mengxn on 15-10-20.
 */
public class FloatButton extends ImageButton {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private DisplayMetrics displayMetrics;
    private GestureDetector gestureDetector;
    private boolean isFloat;

    private static final String TAG = "FloatButton";

    public FloatButton(Context context) {
        this(context, null);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

        gestureDetector = new GestureDetector(context, onGestureListener);

        setBackgroundResource(0);
    }

    public void show(boolean isFloat) {
        this.isFloat = isFloat;
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

    public boolean isFloat() {
        return isFloat;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                move(getStickX(event.getRawX()), event.getRawY());
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void move(float x, float y) {
        params.x = (int) (x - getWidth()/2);
        params.y = (int) (y - getHeight());
        windowManager.updateViewLayout(this, params);
    }

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("FloatButton", String.format(Locale.getDefault(), "x:%f,x1:%f", e1.getRawX(), e2.getRawX()));
            move(e2.getRawX(), e2.getRawY());
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (getOnTouchListener() != null) {
                getOnTouchListener().onClick(FloatButton.this);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (getOnTouchListener() != null) {
                getOnTouchListener().onDoubleClick(FloatButton.this);
            }
            return super.onDoubleTap(e);
        }
    };

    private float getStickX(float x) {
        if (isHalfScreenWidth(x)) {
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    private boolean isHalfScreenWidth(float x) {
        return x - displayMetrics.widthPixels / 2 > 0;
    }

    private OnTouchListener onTouchListener;

    public OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public interface OnTouchListener {

        void onClick(View view);

        void onDoubleClick(View view);
    }
}
