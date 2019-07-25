package com.sherlockshi.pushrefreshpractise;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 20;

    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView rvNumber;
    private BaseQuickAdapter<String, BaseViewHolder> mAdapterNumber;
    private View mEmptyView;

    private int mNextRequestPage = 1;
    private boolean isRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
    }

    private void initRecyclerView() {
        mSmartRefreshLayout = findViewById(R.id.refresh_layout);

        rvNumber = findViewById(R.id.rv_number);

        mEmptyView = getLayoutInflater().inflate(R.layout.include_no_data, (ViewGroup) rvNumber.getParent(), false);

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });

        // 不使用 SmartRefreshLayout 的加载更多功能
        mSmartRefreshLayout.setEnableLoadMore(false);

        mAdapterNumber = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_number) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_number, item);

                helper.addOnClickListener(R.id.tv_number);
            }
        };

        // item 点击事件
        mAdapterNumber.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "点击了第" + (position+1) + "行", Toast.LENGTH_SHORT).show();
            }
        });

        // item 子元素点击事件
        mAdapterNumber.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "点击了" + mAdapterNumber.getData().get(position), Toast.LENGTH_SHORT).show();
            }
        });

        // 加载更多
        mAdapterNumber.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });

        // 横向分割线
        rvNumber.setLayoutManager(new LinearLayoutManager(this));
        rvNumber.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .colorResId(R.color.colorFFF5F5F5)
                        .sizeResId(R.dimen.dp_1)
                        .marginResId(R.dimen.dp_24, R.dimen.dp_24)
                        .build());

        // 竖向分割线
//        rvNumber.setLayoutManager(new GridLayoutManager(this, 2));
//        rvNumber.addItemDecoration(new DividerGridItemDecoration(getResources().getDrawable(R.drawable.colorFFF5F5F5)));

        rvNumber.setAdapter(mAdapterNumber);

        // 设置数据
        List<String> numberList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            numberList.add(i + "");
        }
        mAdapterNumber.setNewData(numberList);
    }

    private void refresh() {
        mNextRequestPage = 1;
        isRefresh = true;

        mAdapterNumber.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        new Request(mNextRequestPage, new RequestCallBack() {
            @Override
            public void success(List<String> data) {
                afterRequestData(data);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                afterRequestDataError("网络错误");
            }
        }).start();
    }

    private void loadMore() {
        isRefresh = false;

        new Request(mNextRequestPage, new RequestCallBack() {
            @Override
            public void success(List<String> data) {
                /**
                 * fix https://github.com/CymChad/BaseRecyclerViewAdapterHelper/issues/2400
                 */
                afterRequestData(data);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                afterRequestDataError("网络错误");
            }
        }).start();
    }

    /**
     *
     * @param msg
     */
    private void afterRequestDataError(String msg) {
//        if (!StringUtil.isEmpty(msg)) {
//            ToastUtils.showWarn(msg);
//        }

        if (isRefresh) {
            if (mEmptyView.getParent() != null) {
                ((ViewGroup) mEmptyView.getParent()).removeView(mEmptyView);
            }
            mAdapterNumber.setEmptyView(mEmptyView);

            mAdapterNumber.setNewData(null);
            mAdapterNumber.setEnableLoadMore(true);
            mSmartRefreshLayout.finishRefresh();
        } else {
            mAdapterNumber.loadMoreFail();
        }
    }

    private void afterRequestData(List<String> data) {
        mNextRequestPage++;
        final int size = data == null ? 0 : data.size();

        if (isRefresh) {
            if (size <= 0) {
                afterRequestDataError("");
            } else {
                mAdapterNumber.setNewData(data);
                mAdapterNumber.setEnableLoadMore(true);
                mSmartRefreshLayout.finishRefresh();
            }
        } else {
            if (size > 0) {
                mAdapterNumber.addData(data);
            }
        }

        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapterNumber.loadMoreEnd(isRefresh);
        } else {
            mAdapterNumber.loadMoreComplete();
        }
    }
}

class Request extends Thread {
    private static final int PAGE_SIZE = 20;
    private int mPage;
    private RequestCallBack mCallBack;
    private Handler mHandler;

    private static boolean mFirstPageNoMore;
    private static boolean mFirstError = true;

    public Request(int page, RequestCallBack callBack) {
        mPage = page;
        mCallBack = callBack;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        try {Thread.sleep(500);} catch (InterruptedException e) {}

        if (mPage == 2 && mFirstError) {
            mFirstError = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallBack.fail(new RuntimeException("fail"));
                }
            });
        } else {
            int size = PAGE_SIZE;
            if (mPage == 1) {
                if (mFirstPageNoMore) {
                    size = 0;
//                    size = 3;
                }
                mFirstPageNoMore = !mFirstPageNoMore;
                if (!mFirstError) {
                    mFirstError = true;
                }
            } else if (mPage == 4) {
                size = 1;
            }

            final int dataSize = size;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallBack.success(getSampleData(dataSize));
                }
            });
        }
    }

    private List<String> getSampleData(int dataSize) {
        List<String> numberList = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            numberList.add(i + "");
        }
        return numberList;
    }
}

interface RequestCallBack {
    void success(List<String> data);

    void fail(Exception e);
}
