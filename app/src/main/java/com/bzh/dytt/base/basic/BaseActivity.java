package com.bzh.dytt.base.basic;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.bzh.common.utils.SharePreferenceUtil;
import com.bzh.dytt.base.eventbus.EventCenter;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * ==========================================================<br>
 * ==========================================================<br>
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ActivityConfig activityConfig;

    private Unbinder mBind;

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activityConfig = new ActivityConfig();
        initUIConfig(activityConfig);
        super.onCreate(savedInstanceState);

        setContentView(getContentViewResId());

        //Activity的配置信息，如果使用butterknife就绑定
        if (activityConfig.isApplyButterKnife) {
            mBind = ButterKnife.bind(this);
        }

        if (activityConfig.isApplyEventBus) {
            EventBus.getDefault().register(this);
        }

        //状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Translucent status bar
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        if (activityConfig.isApplyButterKnife) {
            mBind.unbind();
        }
        if (activityConfig.isApplyEventBus) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }


    /**
     * 初始化Activity配置信息
     * 
     * @param activityConfig Activity配置信息
     */
    protected void initUIConfig(ActivityConfig activityConfig) {

    }

    protected final void onEventMainThread(EventCenter eventCenter) {
        if (eventCenter != null) {
            onEventComing(eventCenter);
        }
    }

    protected void onEventComing(EventCenter eventCenter) {
    }

    protected abstract int getContentViewResId();

    //Activity配置类
    protected final static class ActivityConfig extends UIConfig {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    

    @Override
    public void onBackPressed() {
        /*if (this instanceof MainActivity) {
            if (System.currentTimeMillis() - mPreTime > 2000) {
                //第一个参数必须为getApplicationContext()
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mPreTime = System.currentTimeMillis();
                return;
            }
        }*/
        super.onBackPressed();
    }

    /**
     * show toast
     *
     */
//    protected void showToast(String msg) {
//        //防止遮盖虚拟按键
//        if (null != msg && !TextUtils.isEmpty(msg)) {
//            Snackbar.make(getLoadingTargetView(), msg, Snackbar.LENGTH_SHORT).show();
//        }
//    }

    /*
    设置toolbar
     */
    public int setToolBar(FloatingActionButton floatingActionButton, Toolbar toolbar, boolean isChangeToolbar,
                          boolean isChangeStatusBar, DrawerLayout drawerLayout
                          ) {
        int vibrantColor = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.VIBRANT, 0);
        int mutedColor = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.MUTED, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (SharePreferenceUtil.isChangeNavColor(this))
                getWindow().setNavigationBarColor(vibrantColor);
            else
                getWindow().setNavigationBarColor(Color.BLACK);
        }
        if (floatingActionButton != null)
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(mutedColor));
        if (isChangeToolbar)
            toolbar.setBackgroundColor(vibrantColor);

        Log.e("标题", "标题" + SharePreferenceUtil.isImmersiveMode(this));
        
        if (isChangeStatusBar) {
            if (SharePreferenceUtil.isImmersiveMode(this))
                StatusBarUtil.setColorNoTranslucent(this, vibrantColor);
            else
                StatusBarUtil.setColor(this, vibrantColor);
        }
        if (drawerLayout != null) {
            if (SharePreferenceUtil.isImmersiveMode(this))
                StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawerLayout, vibrantColor);
            else
                StatusBarUtil.setColorForDrawerLayout(this, drawerLayout, vibrantColor);
        }

        return vibrantColor;
    }

}
