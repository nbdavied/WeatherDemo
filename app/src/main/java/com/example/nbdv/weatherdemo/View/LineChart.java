package com.example.nbdv.weatherdemo.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.nbdv.weatherdemo.R;

/**
 * TODO: document your custom view class.
 */
public class LineChart extends View {
    private float y_min;
    private float y_max;
    private float x_min;
    private float x_max;
    private float high_text_size;
    private float low_text_size;
    private float radius;
    private int high_text_color;
    private int low_text_color;
    private int high_color;
    private int low_color;
    private int[] low_temp;
    private int[] high_temp;
    private Paint hPaint;
    private Paint lPaint;
    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LineChart, 0, 0);
        try {
            y_min = typedArray.getDimension(R.styleable.LineChart_y_min, 0);
            y_max = typedArray.getDimension(R.styleable.LineChart_y_max, 10);
            x_min = typedArray.getDimension(R.styleable.LineChart_x_min, 10);
            x_max = typedArray.getDimension(R.styleable.LineChart_x_max, 0);
            high_text_size = typedArray.getDimension(R.styleable.LineChart_high_text_size, 20);
            low_text_size = typedArray.getDimension(R.styleable.LineChart_low_text_size, 20);
            radius = typedArray.getDimension(R.styleable.LineChart_radius, 3);
            high_text_color = typedArray.getColor(R.styleable.LineChart_high_text_color, Color.WHITE);
            low_text_color = typedArray.getColor(R.styleable.LineChart_low_text_color, Color.WHITE);
            high_color = typedArray.getColor(R.styleable.LineChart_high_color, Color.RED);
            low_color = typedArray.getColor(R.styleable.LineChart_low_color, Color.BLUE);
            setMinimumHeight(150);
        } finally {
            typedArray.recycle();
        }

        hPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        hPaint.setColor(high_color);
        lPaint.setColor(low_color);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(x_max==0)
            x_max=getWidth()-20;
        if(y_min==0)
            y_min=getHeight()-20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (high_temp == null || low_temp == null)
            return;
        if(high_temp.length!=low_temp.length||high_temp.length<=1)
            return;
        float x_distance=(x_max-x_min)/(high_temp.length-1);
        //查找最低温度和最高温度
        int lowestTemp=low_temp[0];
        int highestTemp=high_temp[0];
        for(int i=0;i<high_temp.length;i++)
        {
            if(low_temp[i]<lowestTemp)
                lowestTemp=low_temp[i];
            if(high_temp[i]>highestTemp)
                highestTemp=high_temp[i];
        }
        float tempDifference=highestTemp-lowestTemp;
        float yDifference=y_min-y_max;
        float x,ly,hy;
        float last_x=0,last_ly=0,last_hy=0;
        //开始画点
        for(int i=0;i<high_temp.length;i++){
            x=x_min+i*x_distance;
            ly=y_min-(low_temp[i]-lowestTemp)/tempDifference*yDifference;
            hy=y_min-(high_temp[i]-lowestTemp)/tempDifference*yDifference;
            canvas.drawCircle(x,ly,radius,lPaint);
            canvas.drawCircle(x,hy,radius,hPaint);
            if(i!=0)
            {
                canvas.drawLine(last_x,last_ly,x,ly,lPaint);
                canvas.drawLine(last_x,last_hy,x,hy,hPaint);
            }

            last_x=x;
            last_ly=ly;
            last_hy=hy;

        }
    }

    public void setTemperature(int[] low_temp, int[] high_temp) {
        this.high_temp = high_temp;
        this.low_temp = low_temp;
        invalidate();
    }

}
