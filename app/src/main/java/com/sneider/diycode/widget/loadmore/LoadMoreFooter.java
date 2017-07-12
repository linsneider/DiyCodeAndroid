package com.sneider.diycode.widget.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sneider.diycode.R;

public class LoadMoreFooter extends LinearLayout {

    private LinearLayout mLoadingLayout;
    private LinearLayout mEndLayout;
    private LinearLayout mErrorLayout;
    private TextView mBtnReloading;
    private OnClickReloadListener mListener;

    public LoadMoreFooter(Context context) {
        super(context);
        initView(context);
    }

    public LoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(final Context context) {
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = LayoutInflater.from(context).inflate(R.layout.layout_footer, null);
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        mEndLayout = (LinearLayout) view.findViewById(R.id.end_layout);
        mErrorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        mBtnReloading = (TextView) view.findViewById(R.id.reloading);
        mBtnReloading.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onClickReload();
            }
        });
        addView(view);
    }

    public void setOnClickReloadListener(OnClickReloadListener listener) {
        mListener = listener;
    }

    /**
     * 设置到底了
     */
    public void setEnd() {
        setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mErrorLayout.setVisibility(GONE);
        mEndLayout.setVisibility(VISIBLE);
    }

    /**
     * 设置出现错误
     */
    public void setError() {
        setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mErrorLayout.setVisibility(VISIBLE);
        mEndLayout.setVisibility(GONE);
    }

    public void setVisible() {
        setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(VISIBLE);
        mEndLayout.setVisibility(GONE);
        mErrorLayout.setVisibility(GONE);
    }

    public void setGone() {
        setVisibility(GONE);
    }

    /**
     * 设置底部加载中布局
     *
     * @param view
     */
    public void addFootLoadingView(View view) {
        mLoadingLayout.removeAllViews();
        mLoadingLayout.addView(view);
    }

    /**
     * 设置底部到底了布局
     *
     * @param view
     */
    public void addFootEndView(View view) {
        mEndLayout.removeAllViews();
        mEndLayout.addView(view);
    }

    public interface OnClickReloadListener {

        void onClickReload();
    }
}
