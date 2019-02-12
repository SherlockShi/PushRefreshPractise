package com.sherlockshi.pushrefreshpractise.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/********************
 * 作者：yms
 * 日期：2018/1/26  时间：17:41 
 * 邮箱：380413512@qq.com
 * 公司：易联众易惠科技有限公司
 * 注释：
 ********************/
public class CustomHeadView extends RelativeLayout implements RefreshHeader {
    private AVLoadingIndicatorView mAVLoadingIndicatorView;

    public CustomHeadView(Context context) {
        this(context, null);
    }

    public CustomHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        mAVLoadingIndicatorView = new AVLoadingIndicatorView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        mAVLoadingIndicatorView.setLayoutParams(params);

        addView(mAVLoadingIndicatorView);
    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int extendHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        mAVLoadingIndicatorView.smoothToShow();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mAVLoadingIndicatorView.smoothToHide();
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }
}
