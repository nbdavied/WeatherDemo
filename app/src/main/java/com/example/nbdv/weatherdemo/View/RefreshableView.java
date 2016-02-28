package com.example.nbdv.weatherdemo.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nbdv.weatherdemo.R;

/**
 * Created by nbdav on 2016/2/25.
 */
public class RefreshableView extends LinearLayout implements View.OnTouchListener {
    //下拉状态
    public static final int STATUS_PULL_TO_REFRESH = 0;
    //释放刷新状态
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    //刷新中
    public static final int STATUS_REFRESHING = 2;
    //刷新完毕
    public static final int STATUS_REFRESH_FINISHED = 3;
    //头部回滚速度
    public static final int SCROLL_SPEED = -20;
    //当前状态
    private int currentStatus = STATUS_REFRESH_FINISHED;
    //记录上一状态
    private int lastStatus = currentStatus;
    //存储刷新时间
    private SharedPreferences preferences;
    //下拉头view
    private View header;
    //控件
    private ProgressBar pbPullProgress;
    private TextView tvDescription;
    private TextView tvUpdatedAt;
    private ImageView ivArrow;
    //滑动前可以移动的最大距离
    private int touchSlop;
    //是否已经加载了一次
    private boolean loadOnce;
    //header高度
    private int hideHeaderHeight;
    //布局参数
    /**
     * 手指按下时的屏幕纵坐标
     */
    private float yDown;
    //头部layout参数
    private MarginLayoutParams headerMarginLayoutParams;
    //监听器
    private PullToRefreshListener mListener;


    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, null, true);
        pbPullProgress = (ProgressBar) header.findViewById(R.id.pull_progress);
        tvDescription = (TextView) header.findViewById(R.id.description);
        tvUpdatedAt = (TextView) header.findViewById(R.id.updated_at);
        ivArrow = (ImageView) header.findViewById(R.id.ivArrow);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOrientation(VERTICAL);
        addView(header, 0);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            hideHeaderHeight = -header.getHeight();
            headerMarginLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerMarginLayoutParams.topMargin = hideHeaderHeight;
            View view = getChildAt(1);
            view.setOnTouchListener(this);
            loadOnce = true;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yDown = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float YMove = event.getRawY();
                int distance = (int) (YMove - yDown);
                //如手指为上滑状态且下拉头完全，则屏蔽下拉事件
                if (distance <= 0 && headerMarginLayoutParams.topMargin <= hideHeaderHeight)
                    return false;
                if (distance < touchSlop)
                    return false;
                if (currentStatus != STATUS_REFRESHING) {
                    if (headerMarginLayoutParams.topMargin > 0) {
                        currentStatus = STATUS_RELEASE_TO_REFRESH;
                    } else {
                        currentStatus = STATUS_PULL_TO_REFRESH;
                    }
                    //实现下拉效果
                    headerMarginLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
                    header.setLayoutParams(headerMarginLayoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                if (currentStatus == STATUS_RELEASE_TO_REFRESH)
                    new RefreshingTask().execute();
                else if (currentStatus == STATUS_PULL_TO_REFRESH)
                    new HideHeaderTask().execute();

        }
        // 时刻记得更新下拉头中的信息
        if (currentStatus == STATUS_PULL_TO_REFRESH
                || currentStatus == STATUS_RELEASE_TO_REFRESH) {
            updateHeaderView();
            // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态

            lastStatus = currentStatus;
            // 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件
            return true;
        }
        return true;
    }

    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                tvDescription.setText("Pull  to refresh");
                ivArrow.setVisibility(VISIBLE);
                pbPullProgress.setVisibility(GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                tvDescription.setText("Release to refresh");
                ivArrow.setVisibility(VISIBLE);
                pbPullProgress.setVisibility(GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {
                tvDescription.setText("Refreshing");
                ivArrow.setVisibility(GONE);
                pbPullProgress.setVisibility(VISIBLE);
            }

        }
    }

    private void rotateArrow() {
        float pivotX = ivArrow.getWidth() / 2f;
        float pivotY = ivArrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation rotateAnimation=new RotateAnimation(fromDegrees,toDegrees,pivotX,pivotY);
        rotateAnimation.setDuration(100);
        rotateAnimation.setFillAfter(true);
        ivArrow.startAnimation(rotateAnimation);
    }

    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerMarginLayoutParams.topMargin;
            while (true) {
                topMargin += SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null)
                mListener.onRefresh();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            headerMarginLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerMarginLayoutParams);
        }
    }

    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerMarginLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                //sleep(10);
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerMarginLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerMarginLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerMarginLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerMarginLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    //注册监听器
    public void setOnRefreshListener(PullToRefreshListener listener) {
        mListener = listener;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface PullToRefreshListener {
        void onRefresh();
    }
    public void finishRefreshing(){
        currentStatus=STATUS_REFRESH_FINISHED;
        new HideHeaderTask().execute();
    }
}
