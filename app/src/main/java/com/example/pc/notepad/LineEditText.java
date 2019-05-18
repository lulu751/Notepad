package com.example.pc.notepad;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


public class LineEditText extends AppCompatEditText {
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint.setColor(-7829368);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int lineCount = this.getLineCount();
        Rect r = this.mRect;
        Paint p = this.mPaint;

        for(int i = 0; i < lineCount; ++i) {
            int baseline = this.getLineBounds(i, r);
            canvas.drawLine((float)r.left, (float)(baseline + 20), (float)r.right, (float)(baseline + 20), p);
        }

    }
}
