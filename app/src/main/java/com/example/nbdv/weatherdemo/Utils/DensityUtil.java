package com.example.nbdv.weatherdemo.Utils;

import android.content.Context;

/**
 * Created by nbdav on 2016/3/21.
 */
public class DensityUtil {
    /*
    * dp转换为px
     */
    public static int dp2px(Context context,float dp){
        float scale=context.getResources().getDisplayMetrics().density;
        //return px
        return(int)(dp*scale+0.5f);
    }
    /*
    * px转换为dp
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
