package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerAddNewsComponent;
import com.sneider.diycode.di.module.AddNewsModule;
import com.sneider.diycode.mvp.contract.AddNewsContract;
import com.sneider.diycode.mvp.model.bean.NewsNode;
import com.sneider.diycode.mvp.presenter.AddNewsPresenter;
import com.sneider.diycode.utils.PrefUtils;
import com.sneider.diycode.widget.FlowLayout;
import com.sneider.diycode.widget.tag.TagAdapter;
import com.sneider.diycode.widget.tag.TagFlowLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;

import static com.sneider.diycode.app.ARouterPaths.NEWS_ADD;

@Route(path = NEWS_ADD)
public class AddNewsActivity extends BaseActivity<AddNewsPresenter> implements AddNewsContract.View {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.title) TextInputLayout mTitle;
    @BindView(R.id.et_title) EditText mEtTitle;
    @BindView(R.id.link) TextInputLayout mLink;
    @BindView(R.id.et_link) EditText mEtLink;
    @BindView(R.id.flow_layout) TagFlowLayout mFlowLayout;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;
    @BindColor(R.color.color_999999) int color_999999;

    private MaterialDialog mDialog;
    private AppComponent mAppComponent;
    private RxPermissions mRxPermissions;
    private int mNodeId;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        mRxPermissions = new RxPermissions(this);
        DaggerAddNewsComponent.builder().appComponent(appComponent)
                .addNewsModule(new AddNewsModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_add_news;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setTitle("创建新分享");
        setSupportActionBar(mToolbar);
        mDialog = new MaterialDialog.Builder(this).content(R.string.please_wait).progress(true, 0).build();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (action != null && action.equals(Intent.ACTION_SEND) && type != null) {
            if (type.equals("text/plain")) {
                String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                mEtTitle.setText(subject);
                mEtLink.setText(text);
            }
        }

        String json = PrefUtils.getInstance(this).getString("news_nodes", "");
        if (!TextUtils.isEmpty(json)) {
            final List<NewsNode> list = mAppComponent.gson().fromJson(json, new TypeToken<List<NewsNode>>() {
            }.getType());
            initTagLayout(list);
        } else {
            mPresenter.getNewsNodes(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_add_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_send) {
            String title = mEtTitle.getText().toString().trim();
            String link = mEtLink.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                mTitle.setError("请输入标题");
            } else if (TextUtils.isEmpty(link)) {
                mTitle.setErrorEnabled(false);
                mLink.setError("请输入链接");
            } else if (!URLUtil.isNetworkUrl(link)) {
                mLink.setError("链接格式不正确");
            } else if (mNodeId == 0) {
                mTitle.setErrorEnabled(false);
                mLink.setErrorEnabled(false);
                ToastUtils.showShort("请选择分类");
            } else {
                mTitle.setErrorEnabled(false);
                mLink.setErrorEnabled(false);
                mPresenter.createNews(title, link, mNodeId);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetNodes(List<NewsNode> nodes) {
        initTagLayout(nodes);
    }

    private void initTagLayout(List<NewsNode> nodes) {
        TagAdapter adapter = new TagAdapter<NewsNode>(nodes) {
            @Override
            public View getView(FlowLayout parent, int position, NewsNode node) {
                TextView view = (TextView) View.inflate(AddNewsActivity.this, R.layout.item_news_node, null);
                view.setText(node.getName());
                return view;
            }
        };
        mFlowLayout.setOnTagClickListener((view, position, parent) -> {
            mNodeId = nodes.get(position).getId();
            return true;
        });
        mFlowLayout.setAdapter(adapter);
        adapter.setSelectedList(0);
        mNodeId = nodes.get(0).getId();
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public void showLoading() {
        mDialog.show();
    }

    @Override
    public void hideLoading() {
        mDialog.cancel();
    }

    @Override
    public void showMessage(String message) {
        UiUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(Intent intent) {
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .content("退出此次编辑？")
                .contentColor(color_4d4d4d)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> super.onBackPressed())
                .negativeText(R.string.cancel)
                .negativeColor(color_999999)
                .show();
    }
}
