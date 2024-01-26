package com.kimoo.android.extra;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class YatayTextView extends AppCompatTextView {

    public YatayTextView(Context context) {
        super(context);
    }

    public YatayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YatayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(),getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        canvas.save();
        canvas.translate(0f, (float) getHeight());
        canvas.rotate(-90f);
        getLayout().draw(canvas);
        canvas.restore();
    }

}
