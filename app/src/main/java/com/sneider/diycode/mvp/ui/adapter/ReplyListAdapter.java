package com.sneider.diycode.mvp.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.Reply;
import com.sneider.diycode.utils.DateUtils;
import com.sneider.diycode.utils.html.HtmlUtils;

import java.util.List;

import butterknife.BindView;

public class ReplyListAdapter extends DefaultAdapter<Reply> {

    public ReplyListAdapter(List<Reply> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<Reply> getHolder(View v, int viewType) {
        return new ReplyItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_reply_user;
    }

    private OnItemClickListener mOnItemClickListener;
    private HtmlUtils.Callback mCallback;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setCallback(HtmlUtils.Callback callBack) {
        mCallback = callBack;
    }

    class ReplyItemHolder extends BaseHolder<Reply> {

        @BindView(R.id.tv_title) TextView mTvTitle;
        @BindView(R.id.tv_time) TextView mTvTime;
        @BindView(R.id.tv_content) TextView mTvContent;

        ReplyItemHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(Reply data, int position) {
            mTvTitle.setText(data.getTopic_title());
            String intervalTime = DateUtils.getIntervalTime(data.getCreated_at());
            mTvTime.setText(intervalTime);
            HtmlUtils.parseHtmlAndSetText(itemView.getContext(), data.getBody_html(), mTvContent, mCallback);
            itemView.setTag(data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (Reply) v.getTag());
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Reply data);
    }
}
