package com.example.pc.notepad;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;

public class TouchView extends View {
    private Bitmap mBitmap;
    private Bitmap myBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private Handler bitmapHandler;
    GetCutBitmapLocation getCutBitmapLocation;
    private Timer timer;
    DisplayMetrics dm = new DisplayMetrics();
    private int w;
    private int h;
    private int currentColor = -65536;
    private int currentSize = 5;
    private int[] paintColor = new int[]{-65536, -16776961, -16777216, -16711936, -256, -16711681, -3355444};
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    TouchView.this.myBitmap = TouchView.this.getCutBitmap(TouchView.this.mBitmap);
                    Message message = new Message();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bitmap", TouchView.this.myBitmap);
                    message.setData(bundle);
                    TouchView.this.bitmapHandler.sendMessage(message);
                    TouchView.this.RefershBitmap();
                default:
                    super.handleMessage(msg);
            }
        }
    };
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            Log.i("线程", "来了");
            TouchView.this.handler.sendMessage(message);
        }
    };
    private float mX;
    private float mY;
    private static final float TOUCH_TOLERANCE = 4.0F;

    public TouchView(Context context) {
        super(context);
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(this.dm);
        this.w = this.dm.widthPixels;
        this.h = this.dm.heightPixels;
        this.initPaint();
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(this.dm);
        this.w = this.dm.widthPixels;
        this.h = this.dm.heightPixels;
        this.initPaint();
    }

    public void setHandler(Handler mBitmapHandler) {
        this.bitmapHandler = mBitmapHandler;
    }

    private void initPaint() {
        this.setPaintStyle();
        this.getCutBitmapLocation = new GetCutBitmapLocation();
        this.mBitmap = Bitmap.createBitmap(this.w, this.h, Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mBitmap);
        this.mCanvas.drawColor(0);
        this.mPath = new Path();
        this.mBitmapPaint = new Paint(4);
        this.timer = new Timer(true);
    }

    public void setPaintStyle() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth((float)this.currentSize);
        this.mPaint.setColor(this.currentColor);
    }

    public void selectHandWritetSize(int which) {
        int size = Integer.parseInt(this.getResources().getStringArray(2131034113)[which]);
        this.currentSize = size;
        this.setPaintStyle();
    }

    public void selectHandWriteColor(int which) {
        this.currentColor = this.paintColor[which];
        this.setPaintStyle();
    }

    public Bitmap getCutBitmap(Bitmap mBitmap) {
        float cutLeft = this.getCutBitmapLocation.getCutLeft() - 10.0F;
        float cutTop = this.getCutBitmapLocation.getCutTop() - 10.0F;
        float cutRight = this.getCutBitmapLocation.getCutRight() + 10.0F;
        float cutBottom = this.getCutBitmapLocation.getCutBottom() + 10.0F;
        cutLeft = 0.0F > cutLeft ? 0.0F : cutLeft;
        cutTop = 0.0F > cutTop ? 0.0F : cutTop;
        cutRight = (float)mBitmap.getWidth() < cutRight ? (float)mBitmap.getWidth() : cutRight;
        cutBottom = (float)mBitmap.getHeight() < cutBottom ? (float)mBitmap.getHeight() : cutBottom;
        float cutWidth = cutRight - cutLeft;
        float cutHeight = cutBottom - cutTop;
        Bitmap cutBitmap = Bitmap.createBitmap(mBitmap, (int)cutLeft, (int)cutTop, (int)cutWidth, (int)cutHeight);
        if (this.myBitmap != null) {
            this.myBitmap.recycle();
            this.myBitmap = null;
        }

        return cutBitmap;
    }

    private void RefershBitmap() {
        this.initPaint();
        this.invalidate();
        if (this.task != null) {
            this.task.cancel();
        }

    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, 0.0F, 0.0F, this.mBitmapPaint);
        canvas.drawPath(this.mPath, this.mPaint);
    }

    private void touch_start(float x, float y) {
        this.mPath.reset();
        this.mPath.moveTo(x, y);
        this.mX = x;
        this.mY = y;
        if (this.task != null) {
            this.task.cancel();
        }

        this.task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                Log.i("线程", "来了");
                TouchView.this.handler.sendMessage(message);
            }
        };
        this.getCutBitmapLocation.setCutLeftAndRight(this.mX, this.mY);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - this.mX);
        float dy = Math.abs(y - this.mY);
        if (dx >= 4.0F || dy >= 4.0F) {
            this.mPath.quadTo(this.mX, this.mY, x, y);
            this.mX = x;
            this.mY = y;
            if (this.task != null) {
                this.task.cancel();
            }

            this.task = new TimerTask() {
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    Log.i("线程", "来了");
                    TouchView.this.handler.sendMessage(message);
                }
            };
            this.getCutBitmapLocation.setCutLeftAndRight(this.mX, this.mY);
        }

    }

    private void touch_up() {
        this.mCanvas.drawPath(this.mPath, this.mPaint);
        this.mPath.reset();
        if (this.timer != null) {
            if (this.task != null) {
                this.task.cancel();
                this.task = new TimerTask() {
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        TouchView.this.handler.sendMessage(message);
                    }
                };
                this.timer.schedule(this.task, 1000L, 1000L);
            }
        } else {
            this.timer = new Timer(true);
            this.timer.schedule(this.task, 1000L, 1000L);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()) {
            case 0:
                this.touch_start(x, y);
                this.invalidate();
                break;
            case 1:
                this.touch_up();
                this.invalidate();
                break;
            case 2:
                this.touch_move(x, y);
                this.invalidate();
        }

        return true;
    }
}
