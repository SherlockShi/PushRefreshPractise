package com.sherlockshi.pushrefreshpractise;

import android.app.Application;
import android.content.Context;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.sherlockshi.pushrefreshpractise.widget.CustomHeadView;

/**
 * Author:      SherlockShi
 * Email:       sherlock_shi@163.com
 * Date:        2019-02-12 09:50
 * Description:
 */
public class MyApplication extends Application {

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new CustomHeadView(context);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
