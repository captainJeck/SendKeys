package com.linjiaxiaohai.sendkeys.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by mengxn on 15-10-20.
 */
public class FloatButton extends ImageView {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private DisplayMetrics displayMetrics;
    private GestureDetector gestureDetector;
    private boolean isFloat;
    private int defaultHeight = 60;

    private static final String TAG = "FloatButton";

    public FloatButton(Context context) {
        this(context, null);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setScaleType(ScaleType.FIT_CENTER);
        setPadding(10, 10, 10, 10);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = getResources().getDisplayMetrics();
        params = new WindowManager.LayoutParams();
        params.width = (int) (defaultHeight * displayMetrics.density);
        params.height = (int) (defaultHeight * displayMetrics.density);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.y = 0;
        params.x = 0;

        gestureDetector = new GestureDetector(context, onGestureListener);
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
                sendStickyMessage(event);
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void sendStickyMessage(MotionEvent event) {
        Message msg = Message.obtain();
        msg.what = MSG_STICKY;
        msg.obj = event;
        handler.sendMessageDelayed(msg, 50);
    }

    /**
     * 移动到屏幕边缘
     * @param event
     */
    private void moveToScreen(MotionEvent event) {
        move(getStickX(event.getRawX()), event.getRawY());
    }

    private ObjectAnimator animator;

    /**
     * 已动画的形式移动到屏幕边缘
     * @param event
     */
    private void animMoveToScreen(MotionEvent event) {
        if (animator == null) {
            animator = new ObjectAnimator();
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(300);
            animator.setFloatValues(0, 1);
        }
        final float oldX = event.getRawX();
        final float oldY = event.getRawY();
        float newX = getStickX(oldX);
        final float range = newX - oldX;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                move(range * value + oldX, oldY);
            }
        });
        animator.start();
    }

    private float getStickX(float x) {
        if (isHalfScreenWidth(x)) {
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    private boolean isHalfScreenWidth(float x) {
        return x - displayMetrics.widthPixels / 2 > 0;
    }

    private void move(float x, float y) {
        params.x = (int) (x - getWidth()/2);
        params.y = (int) (y - getHeight());
        windowManager.updateViewLayout(this, params);
    }

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.i(TAG, String.format(Locale.getDefault(), "x:%f,x1:%f", e1.getRawX(), e2.getRawX()));
            move(e2.getRawX(), e2.getRawY());
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.i(TAG, "onSingleTapUp:");
            handler.removeMessages(MSG_STICKY);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Log.i(TAG, String.format(Locale.getDefault(), "onSingleTapConfirmed->x:%f,y:%f", e.getRawX(), e.getRawY()));
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

    private final int MSG_STICKY = 1;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_STICKY:
                    animMoveToScreen((MotionEvent) msg.obj);
                    break;
            }
        }
    };
}
