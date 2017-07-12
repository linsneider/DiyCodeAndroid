package com.sneider.diycode.mvp.ui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jess.arms.base.App;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.base.DefaultAdapter;
import com.jess.arms.di.component.AppComponent;
import com.sneider.diycode.R;
import com.sneider.diycode.mvp.model.bean.Sites;
import com.sneider.diycode.utils.DiycodeUtils;

import java.util.List;

import butterknife.BindView;

public class SitesAdapter extends DefaultAdapter<Sites> {

    public SitesAdapter(List<Sites> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<Sites> getHolder(View v, int viewType) {
        return new SitesItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_sites;
    }

    class SitesItemHolder extends BaseHolder<Sites> {

        @BindView(R.id.tv_name) TextView mTvName;
        @BindView(R.id.list) RecyclerView mList;

        private AppComponent mAppComponent;

        SitesItemHolder(View itemView) {
            super(itemView);
            mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
        }

        @Override
        public void setData(Sites data, int position) {
            mTvName.setText(data.getName());
            GridLayoutManager manager = new GridLayoutManager(mAppComponent.application(), 2);
            mList.setLayoutManager(manager);
            SitesBeanAdapter adapter = new SitesBeanAdapter(data.getSites());
            adapter.setOnItemClickListener((view, sitesBean) -> DiycodeUtils.openWebActivity(sitesBean.getUrl()));
            mList.setAdapter(adapter);
        }
    }
}
