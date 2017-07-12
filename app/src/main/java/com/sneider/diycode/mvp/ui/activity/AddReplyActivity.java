package com.sneider.diycode.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;
import com.sneider.diycode.R;
import com.sneider.diycode.app.BaseActivity;
import com.sneider.diycode.di.component.DaggerAddReplyComponent;
import com.sneider.diycode.di.module.AddReplyModule;
import com.sneider.diycode.mvp.contract.AddReplyContract;
import com.sneider.diycode.mvp.model.bean.News;
import com.sneider.diycode.mvp.model.bean.Project;
import com.sneider.diycode.mvp.model.bean.Topic;
import com.sneider.diycode.mvp.presenter.AddReplyPresenter;
import com.sneider.diycode.utils.DiycodeUtils;
import com.sneider.diycode.utils.MarkdownUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.body.ProgressInfo;

import static com.sneider.diycode.app.ARouterPaths.PUBLIC_MARKDOWN;
import static com.sneider.diycode.app.ARouterPaths.REPLY_ADD;
import static com.sneider.diycode.mvp.ui.activity.MarkdownActivity.EXTRA_CONTENT;

@Route(path = REPLY_ADD)
public class AddReplyActivity extends BaseActivity<AddReplyPresenter> implements AddReplyContract.View, ProgressListener {

    public static final String EXTRA_REPLY_TYPE = "EXTRA_REPLY_TYPE";
    public static final int TYPE_TOPIC = 1;
    public static final int TYPE_NEWS = 2;
    public static final int TYPE_PROJECT = 3;
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_REPLY_ID = "EXTRA_REPLY_ID";
    public static final String EXTRA_REPLY_USER = "EXTRA_REPLY_USER";
    public static final String EXTRA_REPLY_FLOOR = "EXTRA_REPLY_FLOOR";
    private static final int REQ_SELECT_IMAGE = 100;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_title) TextView mTvTitle;
    @BindView(R.id.body) TextInputLayout mBody;
    @BindView(R.id.et_body) EditText mEtBody;
    @BindView(R.id.btn_delete) TextView mBtnDelete;

    @BindColor(R.color.color_4d4d4d) int color_4d4d4d;
    @BindColor(R.color.color_999999) int color_999999;

    private MaterialDialog mDialog;
    private PopupMenu mMenu;
    private int mReplyTpye;
    private int mReplyId;
    private Topic mTopic;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.tv_progress) TextView mTvProgress;
    private ProgressInfo mLastUploadingingInfo;
    private Handler mHandler = new Handler();

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerAddReplyComponent.builder().appComponent(appComponent)
                .addReplyModule(new AddReplyModule(this)).build().inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_add_reply;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setTitle(R.string.reply);
        setSupportActionBar(mToolbar);
        mDialog = new MaterialDialog.Builder(this).content(R.string.please_wait).progress(true, 0).build();

        mReplyTpye = getIntent().getIntExtra(EXTRA_REPLY_TYPE, 0);
        switch (mReplyTpye) {
            case TYPE_TOPIC:
                mTopic = (Topic) getIntent().getSerializableExtra(EXTRA_DATA);
                if (mTopic != null) {
                    mTvTitle.setText(mTopic.getTitle());
                    // 修改回复
                    mReplyId = getIntent().getIntExtra(EXTRA_REPLY_ID, 0);
                    if (mReplyId != 0) {
                        mPresenter.getTopicReply(mReplyId);
                        mBtnDelete.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case TYPE_NEWS:
                News mNews = (News) getIntent().getSerializableExtra(EXTRA_DATA);
                if (mNews != null) {
                    mTvTitle.setText(mNews.getTitle());
                }
                break;
            case TYPE_PROJECT:
                Project mProject = (Project) getIntent().getSerializableExtra(EXTRA_DATA);
                if (mProject != null) {
                    mTvTitle.setText(mProject.getName());
                }
                // 修改回复
                mReplyId = getIntent().getIntExtra(EXTRA_REPLY_ID, 0);
                if (mReplyId != 0) {
                    mPresenter.getProjectReply(mReplyId);
                    mBtnDelete.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        int replyFloor = getIntent().getIntExtra(EXTRA_REPLY_FLOOR, 0);
        String replyUser = getIntent().getStringExtra(EXTRA_REPLY_USER);
        if (replyFloor != 0 && !TextUtils.isEmpty(replyUser)) {
            String text = MessageFormat.format(getString(R.string.reply_prefix), replyFloor, replyUser) + " ";
            mEtBody.setText(text);
            mEtBody.setSelection(text.length());
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
            String body = mEtBody.getText().toString().trim();
            if (TextUtils.isEmpty(body)) {
                mBody.setError(getString(R.string.reply_hint));
            } else {
                mBody.setErrorEnabled(false);
                switch (mReplyTpye) {
                    case TYPE_TOPIC:
                        if (mReplyId != 0) {
                            mPresenter.updateTopicReply(mReplyId, body);
                        } else {
                            if (mTopic != null) {
                                mPresenter.createTopicReply(mTopic.getId(), body);
                            }
                        }
                        break;
                    case TYPE_NEWS:
                        break;
                    case TYPE_PROJECT:
                        if (mReplyId != 0) {
                            mPresenter.updateProjectReply(mReplyId, body);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_delete)
    void clickDelete() {
        new MaterialDialog.Builder(this)
                .content(R.string.delete_hint)
                .contentColor(color_4d4d4d)
                .positiveText(R.string.confirm)
                .onPositive((dialog, which) -> {
                    switch (mReplyTpye) {
                        case TYPE_TOPIC:
                            mPresenter.deleteTopicReply(mReplyId);
                            break;
                        case TYPE_NEWS:
                            break;
                        case TYPE_PROJECT:
                            mPresenter.deleteProjectReply(mReplyId);
                            break;
                        default:
                            break;
                    }
                })
                .negativeText(R.string.cancel)
                .negativeColor(color_999999)
                .show();
    }

    @Override
    public void onGetReply(String reply) {
        mEtBody.setText(reply);
        mEtBody.setSelection(reply.length());
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
