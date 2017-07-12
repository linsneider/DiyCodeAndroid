package com.sneider.diycode.mvp.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.Section;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SectionListAdapter extends DefaultAdapter<Section> {

    public SectionListAdapter(List<Section> infos) {
        super(infos);
    }

    public void addData(List<Section> data) {
        if (mInfos == null) mInfos = new ArrayList<>();
        mInfos.addAll(data);
        notifyDataSetChanged();
    }

    private OnItemClickListener mOnItemClickListener;
    private View mLastView;
    private boolean isInit = true;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public BaseHolder<Section> getHolder(View v, int viewType) {
        return new SectionItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_section;
    }

    class SectionItemHolder extends BaseHolder<Section> {

        @BindView(R.id.tv_name) TextView mTvName;

        SectionItemHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(Section data, int position) {
            mTvName.setText(data.getName());
            itemView.setTag(data);
            itemView.setOnClickListener(this);
            if (isInit && position == 0) {
                itemView.performClick();
                isInit = false;
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                if (mLastView != null) mLastView.setSelected(false);
                v.setSelected(true);
                mLastView = v;
                mOnItemClickListener.onItemClick(v, (Section) v.getTag());
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Section data);
    }
}
