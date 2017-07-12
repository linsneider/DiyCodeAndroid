package com.sneider.diycode.mvp.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NodeListAdapter extends DefaultAdapter<Node> {

    public NodeListAdapter(List<Node> infos) {
        super(infos);
    }

    public void addData(List<Node> data) {
        if (mInfos == null) mInfos = new ArrayList<>();
        mInfos.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        if (mInfos != null) mInfos.clear();
        if (mLastView != null) mLastView.setSelected(false);
        notifyDataSetChanged();
    }

    private OnItemClickListener mOnItemClickListener;
    private View mLastView;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public BaseHolder<Node> getHolder(View v, int viewType) {
        return new NodeItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_node;
    }

    class NodeItemHolder extends BaseHolder<Node> {

        @BindView(R.id.tv_name) TextView mTvName;

        NodeItemHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(Node data, int position) {
            mTvName.setText(data.getName());
            itemView.setTag(data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                if (mLastView != null) mLastView.setSelected(false);
                v.setSelected(true);
                mLastView = v;
                mOnItemClickListener.onItemClick(v, (Node) v.getTag());
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Node data);
    }
}
