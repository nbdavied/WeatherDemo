package com.example.nbdv.weatherdemo.View;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nbdav on 2016/3/13.
 * 设置当横向滑动大于一定距离时，禁止下拉刷新
 * 处理下拉刷新和viewpager横向滑动手势冲突问题
 */
public class VerticalSwipeRefreshLayout extends SwipeRefreshLayout {
    private int touchSlope;
    private float lastX;
    public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX=getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX=ev.getX();
                float xDiff=Math.abs(currentX-lastX);//距离绝对值
                if(xDiff>touchSlope+60)
                    return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
