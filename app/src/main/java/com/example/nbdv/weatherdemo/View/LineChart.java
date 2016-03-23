package com.example.nbdv.weatherdemo.View;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.nbdv.weatherdemo.R;
import com.example.nbdv.weatherdemo.Utils.DensityUtil;
import com.example.nbdv.weatherdemo.json.JsonWeather;

/**
 * TODO: document your custom view class.
 */
public class LineChart extends View {
    private final static int[] dayOfWeek = new int[]{
            R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednsday, R.string.thursday, R.string.friday, R.string.saturday
    };
    private Context context;

    private float y_min;
    private float y_max;
    private float x_min;
    private float x_max;
    private float bottom_dis;
    private float top_dis;
    private float left_dis;
    private float right_dis;
    private float text_size;
    private float radius;
    private int high_text_color;
    private int low_text_color;
    private int high_color;
    private int low_color;
    private float stroke_width;
    //天气数据
    private int[] low_temp;
    private int[] high_temp;
    private int[] condition_code;

    private Paint hPaint;
    private Paint lPaint;
    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LineChart, 0, 0);
        try {
            float defaultMargin=DensityUtil.dp2px(context,10);
            float defaultTextSize=DensityUtil.dp2px(context,22);
            float defaultRadius=DensityUtil.dp2px(context,3);
            float defaultStrokeWidth=2;
            bottom_dis = typedArray.getDimension(R.styleable.LineChart_bottom_dis,DensityUtil.dp2px(context,60));
            top_dis = typedArray.getDimension(R.styleable.LineChart_top_dis, defaultMargin);
            left_dis = typedArray.getDimension(R.styleable.LineChart_left_dis, defaultMargin);
            right_dis = typedArray.getDimension(R.styleable.LineChart_right_dis, defaultMargin);
            text_size = typedArray.getDimension(R.styleable.LineChart_text_size, defaultTextSize);
            radius = typedArray.getDimension(R.styleable.LineChart_radius, defaultRadius);
            stroke_width=typedArray.getDimension(R.styleable.LineChart_stroke_width,defaultStrokeWidth);
            high_color = typedArray.getColor(R.styleable.LineChart_high_color, Color.RED);
            low_color = typedArray.getColor(R.styleable.LineChart_low_color, Color.BLUE);
            high_text_color = typedArray.getColor(R.styleable.LineChart_high_text_color, Color.WHITE);
            low_text_color = typedArray.getColor(R.styleable.LineChart_low_text_color, Color.WHITE);
            setMinimumHeight(DensityUtil.dp2px(context,250));
        } finally {
            typedArray.recycle();
        }

        hPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        hPaint.setColor(high_color);
        lPaint.setColor(low_color);

        hPaint.setTextSize(text_size);
        lPaint.setTextSize(text_size);
        hPaint.setTextAlign(Paint.Align.CENTER);
        lPaint.setTextAlign(Paint.Align.CENTER);


        hPaint.setStrokeWidth(stroke_width);
        lPaint.setStrokeWidth(stroke_width);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        y_min=getHeight()-bottom_dis;
        y_max=top_dis;
        x_min=left_dis;
        x_max=getWidth()-right_dis;
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
        //最高温和最低温差
        float tempDifference=highestTemp-lowestTemp;
        //y轴最大距离差
        float yDifference=y_min-y_max;
        //记录当前点的位置
        float x,ly,hy;
        //记录上一个点的位置
        float last_x=0,last_ly=0,last_hy=0;
        //获取资源文件
        Resources res=getResources();
        Bitmap bitmap;
        int width,height;   //资源文件的宽高
        //int newWidth=DensityUtil.dp2px(context,40);
        //int newHeight=newWidth;
        //float wScale,hScale;    //缩放比例
        int bitX,bitY;
        //Matrix matrix=new Matrix();
        //开始画点
        for(int i=0;i<high_temp.length;i++){
            x=x_min+i*x_distance;
            ly=y_min-(low_temp[i]-lowestTemp)/tempDifference*yDifference;
            hy=y_min-(high_temp[i]-lowestTemp)/tempDifference*yDifference;
            canvas.drawCircle(x, ly, radius, lPaint);
            canvas.drawCircle(x, hy, radius, hPaint);
            canvas.drawText(String.valueOf(high_temp[i]), x, hy - 5 - text_size / 2, hPaint);
            canvas.drawText(String.valueOf(low_temp[i]), x, ly + 15 + text_size / 2, lPaint);
            bitmap= BitmapFactory.decodeResource(res, R.drawable.line_chart_cond);
            width=bitmap.getWidth();
            height=bitmap.getHeight();
            //wScale=((float)width)/newWidth;
            //hScale=((float)height)/newHeight;
            //matrix.postScale(wScale,hScale);
            bitX=(int)(x-width/2);
            bitY=(int)(y_min+20);
            //Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
            canvas.drawBitmap(bitmap,bitX,bitY,null);
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
    public void setData(JsonWeather weather)
    {
        int lenth = weather.serviceVersion[0].daily_forecast.length;
        low_temp=new int[lenth];
        high_temp=new int[lenth];
        condition_code=new int[lenth];
        for (int i = 0; i < lenth; i++) {
            low_temp[i] = weather.serviceVersion[0].daily_forecast[i].tmp.min;
            high_temp[i] = weather.serviceVersion[0].daily_forecast[i].tmp.max;
            condition_code[i]=weather.serviceVersion[0].daily_forecast[i].cond.code_d;
        }
        invalidate();
    }
}
