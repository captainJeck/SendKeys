package com.linjiaxiaohai.sendkeys.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.linjiaxiaohai.sendkeys.utils.FloatKeeper;

/**
 * Created by mengxn on 15-10-20.
 */
public class FloatButton extends ImageView {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private DisplayMetrics displayMetrics;
    private GestureDetector gestureDetector;
    private boolean isFloat = false;
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
        params.type = FloatKeeper.isFloatTop(context) ? WindowManager.LayoutParams.TYPE_SYSTEM_ERROR : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.y = 0;
        params.x = 0;

        gestureDetector = new GestureDetector(context, onGestureListener);

    }

    public void show(boolean isFloat) {
        try {
            if (isFloat) {
                //如果已添加,则更新view
                if (this.isFloat) {
                    windowManager.updateViewLayout(this, params);
                } else {
                    windowManager.addView(this, params);
                }
                sendTranslucentMessage();
            } else {
                windowManager.removeView(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.isFloat = isFloat;
    }

    /**
     * 更新位置
     */
    public void updateLocation() {
        animMoveToScreen(params.x, params.y);
    }

    /**
     * 是否已添加
     * @return
     */
    public boolean isFloat() {
        return isFloat;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeMessages(MSG_TRANSLUCENT);
                setTouchAlpha(255);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                sendStickyMessage(event);
                sendTranslucentMessage();
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

    private void sendTranslucentMessage() {
        handler.sendEmptyMessageDelayed(MSG_TRANSLUCENT, 1500);
    }

    public void setTouchAlpha(int alpha) {
        if (Build.VERSION.SDK_INT >= 16) {
            setImageAlpha(alpha);
        } else {
            if (getDrawable() != null) {
                getDrawable().setAlpha(alpha);
            }
        }
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
        animMoveToScreen(event.getRawX(), event.getRawY());
    }

    /**
     * 已动画的形式移动到屏幕边缘
     * @param x 原位置x
     * @param y 原位置y
     */
    private void animMoveToScreen(final float x, final float y) {
        if (animator == null) {
            animator = new ObjectAnimator();
            animator.setTarget(this);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(300);
            animator.setFloatValues(0, 1);
        }
        float newX = getStickX(x);
        final float range = newX - x;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                move(range * value + x, y);
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
            Log.i(TAG, "onSingleTapUp:"+System.currentTimeMillis());
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
            Log.i(TAG, "onDoubleTap:"+System.currentTimeMillis());
            handler.removeMessages(MSG_STICKY);
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

    private int ALPHA_TRANSLUCENT = 120;

    private final int MSG_STICKY = 1;
    private final int MSG_TRANSLUCENT = 2;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_STICKY:
                    animMoveToScreen((MotionEvent) msg.obj);
                    break;
                case MSG_TRANSLUCENT:
                    setTouchAlpha(ALPHA_TRANSLUCENT);
                    break;
            }
        }
    };

    public enum Operate {

        BACK(0),
        HOME(1),
        LOCK(2);

        private int code;

        Operate(int code) {
            this.code = code;
        }
    }
}
