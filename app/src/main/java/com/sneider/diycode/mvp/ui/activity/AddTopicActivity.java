package com.sneider.diycode.mvp.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerAddTopicComponent;
import com.sneider.diycode.di.module.AddTopicModule;
import com.sneider.diycode.mvp.contract.AddTopicContract;
import com.sneider.diycode.mvp.model.bean.Section;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.presenter.AddTopicPresenter;
import com.sneider.diycode.mvp.ui.adapter.NodeListAdapter;
import com.sneider.diycode.mvp.ui.adapter.SectionListAdapter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.MarkdownUtils;
import com.sneider.diycode.utils.PrefUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_MARKDOWN;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_ADD;
import static com.sneider.diycode.mvp.ui.activity.MarkdownActivity.EXTRA_CONTENT;

@Route(path = TOPIC_ADD)
public class AddTopicActivity extends BaseActivity<AddTopicPresenter> implements AddTopicContract.View {

    public static final String EXTRA_TOPIC = "EXTRA_TOPIC";
    private static final int REQ_SELECT_IMAGE = 100;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_select_node) TextView mBtnSelectNode;
    @BindView(R.id.title) TextInputLayout mTitle;
    @BindView(R.id.et_title) EditText mEtTitle;
    @BindView(R.id.body) TextInputLayout mBody;
    @BindView(R.id.et_body) EditText mEtBody;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.tv_progress) TextView mTvProgress;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;
    @BindColor(R.color.color_999999) int color_999999;

    private AppComponent mAppComponent;
    private RxPermissions mRxPermissions;
    private MaterialDialog mDialog;
    private PopupMenu mMenu;
    private PopupWindow mWindow;
    private SectionListAdapter mSectionListAdapter;
    private NodeListAdapter mNodeListAdapter;
    private Topic mData;
    private int mNodeId;
    private boolean isUpdate;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
        mRxPermissions = new RxPermissions(this);
        DaggerAddTopicComponent.builder().appComponent(appComponent)
                .addTopicModule(new AddTopicModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_add_topic;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mData = (Topic) getIntent().getSerializableExtra(EXTRA_TOPIC);
        if (mData != null) {
            mToolbar.setTitle(R.string.modify_topic);
            isUpdate = true;
            mNodeId = mData.getNode_id();
            mBtnSelectNode.setText(MessageFormat.format(getString(R.string.what_node), mData.getNode_name()));
            String title = mData.getTitle();
            mEtTitle.setText(title);
            mEtTitle.setSelection(title.length());
            String body = mData.getBody();
            mEtBody.setText(body);
            mEtBody.setSelection(body.length());
        } else {
            mToolbar.setTitle(R.string.add_topic);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);
        mDialog = new MaterialDialog.Builder(this).content(R.string.please_wait).progress(true, 0).build();

        initPopupWindow();
        String json = PrefUtils.getInstance(this).getString("topic_nodes", "");
        if (!TextUtils.isEmpty(json)) {
            List<Section> list = mAppComponent.gson().fromJson(json, new TypeToken<List<Section>>() {
            }.getType());
            mSectionListAdapter.addData(list);
            mNodeListAdapter.addData(list.get(0).getNodes());
        } else {
            mPresenter.getNodes(true);
        }
    }

    private void initPopupWindow() {
        View view = View.inflate(this, R.layout.popup_topic_nodes, null);
        RecyclerView listSection = ButterKnife.findById(view, R.id.list_section);
        LinearLayoutManager sectionManager = new LinearLayoutManager(this);
        sectionManager.setOrientation(LinearLayoutManager.VERTICAL);
        listSection.setLayoutManager(sectionManager);
        mSectionListAdapter = new SectionListAdapter(null);
        mSectionListAdapter.setOnItemClickListener((view1, data) -> {
            mNodeListAdapter.clearData();
            mNodeListAdapter.addData(data.getNodes());
        });
        listSection.setAdapter(mSectionListAdapter);

        RecyclerView listNode = ButterKnife.findById(view, R.id.list_node);
        LinearLayoutManager nodeManager = new LinearLayoutManager(this);
        nodeManager.setOrientation(LinearLayoutManager.VERTICAL);
        listNode.setLayoutManager(nodeManager);

        mNodeListAdapter = new NodeListAdapter(null);
        mNodeListAdapter.setOnItemClickListener((view2, data) -> {
            mBtnSelectNode.setText(MessageFormat.format(getString(R.string.what_node), data.getName()));
            mNodeId = data.getId();
            mWindow.dismiss();
        });
        listNode.setAdapter(mNodeListAdapter);

        mWindow = new PopupWindow(view);
        mWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
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
    public void onGetNodes(List<Section> sections) {
        mSectionListAdapter.addData(sections);
        mNodeListAdapter.addData(sections.get(0).getNodes());
    }

    @Override
    public void onUploadPhoto(String url) {
        MarkdownUtils.addImage(mEtBody, "", url);
    }

    @Override
    public void showUploading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTvProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUploading() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTvProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
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
            if (mNodeId == 0) {
                ToastUtils.showShort(R.string.please_choose_node);
                return true;
            }
            String title = mEtTitle.getText().toString().trim();
            String body = mEtBody.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                mTitle.setError(getString(R.string.please_input_title));
            } else if (TextUtils.isEmpty(body)) {
                mTitle.setErrorEnabled(false);
                mBody.setError(getString(R.string.please_input_content));
            } else {
                mTitle.setErrorEnabled(false);
                mBody.setErrorEnabled(false);
                if (isUpdate) {
                    mPresenter.updateTopic(mData.getId(), title, body, mNodeId);
                } else {
                    mPresenter.createTopic(title, body, mNodeId);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_SELECT_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    List<String> paths = Matisse.obtainPathResult(data);
                    for (String path : paths) {
                        mPresenter.uploadPhoto(path);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .content(R.string.quit_edit)
                .contentColor(color_4d4d4d)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> super.onBackPressed())
                .negativeText(R.string.cancel)
                .negativeColor(color_999999)
                .show();
    }

    @OnClick(R.id.btn_select_node)
    void selectNode() {
        if (mWindow != null && mSectionListAdapter.getInfos() != null) {
            mWindow.showAsDropDown(mBtnSelectNode);
        } else {
            mPresenter.getNodes(true);
        }
    }

    @OnClick(R.id.btn_help)
    void help() {
        DiycodeUtils.openWebActivity("https://www.diycode.cc/markdown");
    }

    @OnClick(R.id.btn_insert_code)
    void insertNode(View v) {
        if (mMenu == null) {
            mMenu = MarkdownUtils.createCodePopupMenu(this, v, codeCategory -> MarkdownUtils.addCode(mEtBody, codeCategory));
        }
        mMenu.show();
    }

    @OnClick(R.id.btn_insert_image)
    void insertImage() {
        Matisse.from(this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(3)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.sneider.diycode.fileprovider"))
//                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .theme(R.style.Matisse_DiyCode)
                .forResult(REQ_SELECT_IMAGE);
    }

    @OnClick(R.id.btn_preview)
    void preview() {
        ARouter.getInstance().build(PUBLIC_MARKDOWN)
                .withString(EXTRA_CONTENT, mEtBody.getText().toString().trim())
                .navigation();
    }
}
