package com.sneider.diycode.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.body.ProgressInfo;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_MARKDOWN;
import static com.sneider.diycode.app.ARouterPaths.TOPIC_ADD;
import static com.sneider.diycode.mvp.ui.activity.MarkdownActivity.EXTRA_CONTENT;

@Route(path = TOPIC_ADD)
public class AddTopicActivity extends BaseActivity<AddTopicPresenter> implements AddTopicContract.View, ProgressListener {

    public static final String EXTRA_TOPIC = "EXTRA_TOPIC";
    private static final int REQ_SELECT_IMAGE = 100;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_select_node) TextView mBtnSelectNode;
    @BindView(R.id.title) TextInputLayout mTitle;
    @BindView(R.id.et_title) EditText mEtTitle;
    @BindView(R.id.body) TextInputLayout mBody;
    @BindView(R.id.et_body) EditText mEtBody;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;
    @BindColor(R.color.color_999999) int color_999999;

    private MaterialDialog mDialog;
    private AppComponent mAppComponent;
    private RxPermissions mRxPermissions;
    private boolean isUpdate;
    private Topic mData;
    private int mNodeId;
    private PopupMenu mMenu;
    private PopupWindow mWindow;
    private SectionListAdapter mSectionListAdapter;
    private NodeListAdapter mNodeListAdapter;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.tv_progress) TextView mTvProgress;
    private ProgressInfo mLastUploadingingInfo;
    private Handler mHandler = new Handler();

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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initData(Bundle savedInstanceState) {
        mData = (Topic) getIntent().getSerializableExtra(EXTRA_TOPIC);
        if (mData != null) {
            mToolbar.setTitle("修改话题");
            isUpdate = true;
            mNodeId = mData.getNode_id();
            mBtnSelectNode.setText("节点：" + mData.getNode_name());
            String title = mData.getTitle();
            mEtTitle.setText(title);
            mEtTitle.setSelection(title.length());
            String body = mData.getBody();
            mEtBody.setText(body);
            mEtBody.setSelection(body.length());
        } else {
            mToolbar.setTitle("发布新话题");
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

        // Okhttp/Retofit 上传监听
//        ProgressManager.getInstance().addRequestLisenter(Constant.UPLOAD_URL, this);
    }

    @Override
    public void onProgress(ProgressInfo progressInfo) {
        // 如果你不屏蔽用户重复点击上传或下载按钮,就可能存在同一个 Url 地址,上一次的上传或下载操作都还没结束,
        // 又开始了新的上传或下载操作,那现在就需要用到id(请求开始时的时间) 来区分正在执行的进度信息
        // 这里我就取最新的上传进度用来展示,顺便展示下id的用法
        if (mLastUploadingingInfo == null) {
            mLastUploadingingInfo = progressInfo;
        }
        // 因为是以请求开始时的时间作为id,所以值越大,说明该请求越新
        if (progressInfo.getId() < mLastUploadingingInfo.getId()) {
            return;
        } else if (progressInfo.getId() > mLastUploadingingInfo.getId()) {
            mLastUploadingingInfo = progressInfo;
        }
        int progress = mLastUploadingingInfo.getPercent();
        mTvProgress.setText(progress + "%");
        Log.e(TAG, mLastUploadingingInfo.getId() + "--upload--" + progress + " %  " + mLastUploadingingInfo.getEachBytes() + "  " + mLastUploadingingInfo.getCurrentbytes() + "  " + mLastUploadingingInfo.getContentLength());
        Log.e(TAG, mLastUploadingingInfo.getSpeed() + " byte/s");
        if (mLastUploadingingInfo.isFinish()) {
            // 说明已经上传完成
            Log.e(TAG, "Upload -- finish");
        }
    }

    @Override
    public void onError(long id, Exception e) {
        mHandler.post(() -> mTvProgress.setText("error"));
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
            mBtnSelectNode.setText("节点：" + data.getName());
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

    @OnClick(R.id.btn_select_node)
    void selectNode() {
        if (mWindow != null && mSectionListAdapter.getInfos() != null) {
            mWindow.showAsDropDown(mBtnSelectNode);
        } else {
            mPresenter.getNodes(true);
        }
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

    @OnClick(R.id.btn_help)
    void help() {
        DiycodeUtils.openWebActivity("https://www.diycode.cc/markdown");
    }

    @OnClick(R.id.btn_preview)
    void preview() {
        ARouter.getInstance().build(PUBLIC_MARKDOWN)
                .withString(EXTRA_CONTENT, mEtBody.getText().toString().trim())
                .navigation();
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
            if (mNodeId == 0) {
                UiUtils.snackbarText("请选择节点");
                return true;
            }
            String title = mEtTitle.getText().toString().trim();
            String body = mEtBody.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                mTitle.setError("请输入标题");
            } else if (TextUtils.isEmpty(body)) {
                mTitle.setErrorEnabled(false);
                mBody.setError("请输入内容");
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
