package com.sneider.diycode.widget.loadmore;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

public class LoadMoreRecyclerView extends RecyclerView {

    private Context mContext;
    private LoadMoreListener mLoadMoreListener;
    // 是否可加载更多
    private boolean canloadMore = true;
    private Adapter mAdapter;
    private Adapter mFooterAdapter;
    private boolean isLoadingData = false;
    //加载更多布局
    private LoadMoreFooter loadingMoreFooter;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LoadMoreFooter footView = new LoadMoreFooter(mContext);
        addFootView(footView);
        footView.setGone();
    }

    // 点击监听
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (mFooterAdapter != null && mFooterAdapter instanceof LoadMoreFooterAdapter) {
            ((LoadMoreFooterAdapter) mFooterAdapter).setOnItemClickListener(onItemClickListener);
        }
    }

    // 长按监听
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        if (mFooterAdapter != null && mFooterAdapter instanceof LoadMoreFooterAdapter) {
            ((LoadMoreFooterAdapter) mFooterAdapter).setOnItemLongClickListener(listener);
        }
    }

    /**
     * 底部加载更多视图
     */
    public void addFootView(LoadMoreFooter view) {
        loadingMoreFooter = view;
    }

    /**
     * 设置底部加载中布局
     *
     * @param view
     */
    public void setFootLoadingView(View view) {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.addFootLoadingView(view);
        }
    }

    /**
     * 设置底部到底了布局
     *
     * @param view
     */
    public void setFootEndView(View view) {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.addFootEndView(view);
        }
    }

    public void setOnClickReloadListener(LoadMoreFooter.OnClickReloadListener listener) {
        loadingMoreFooter.setOnClickReloadListener(listener);
    }

    // 下拉刷新后初始化底部状态
    public void refreshComplete() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setGone();
        }
        isLoadingData = false;
    }

    public void showLoadMore() {
        loadingMoreFooter.setVisible();
    }

    public void loadMoreComplete() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setGone();
        }
        isLoadingData = false;
    }

    // 到底了
    public void loadMoreEnd() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setEnd();
        }
    }

    // 出错了
    public void loadMoreError() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setError();
        }
        isLoadingData = false;
    }

    // 设置是否可加载更多
    public void setCanloadMore(boolean flag) {
        canloadMore = flag;
    }

    // 设置加载更多监听
    public void setLoadMoreListener(LoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mFooterAdapter = new LoadMoreFooterAdapter(this, loadingMoreFooter, adapter);
        super.setAdapter(mFooterAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreListener != null && !isLoadingData && canloadMore) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                //
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                //
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = last(into);
            } else {
                //
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {
                if (loadingMoreFooter != null) {
                    showLoadMore();
                }
                isLoadingData = true;
                mLoadMoreListener.onLoadMore();
            }
        }
    }

    /**
     * 取到最后的一个节点
     *
     * @param lastPositions
     * @return
     */
    private int last(int[] lastPositions) {
        int last = lastPositions[0];
        for (int value : lastPositions) {
            if (value > last) {
                last = value;
            }
        }
        return last;
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mFooterAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mFooterAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mFooterAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };
}