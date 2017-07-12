package com.sneider.diycode.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sneider.diycode.R;

public class SettingRowView extends LinearLayout {

    private View mRootView;
    private TextView mSettingTitle;
    private TextView mSettingDescription;

    public SettingRowView(Context context) {
        this(context, null);
    }

    public SettingRowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingRowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRootView = inflate(context, R.layout.view_setting_row, this);
        mSettingTitle = (TextView) findViewById(R.id.setting_title);
        mSettingDescription = (TextView) findViewById(R.id.setting_description);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RowView);
            String title = a.getString(R.styleable.RowView_rowTitle);
            String description = a.getString(R.styleable.RowView_rowDescription);
            mSettingTitle.setText(title);
            mSettingDescription.setText(description);
            a.recycle();
        }
    }

    public void setSettingTitle(String title) {
        mSettingTitle.setText(title);
    }

    public void setSettingDescription(String description) {
        mSettingDescription.setText(description);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mRootView.setEnabled(enabled);
        if (enabled) {
            mSettingTitle.setTextColor(getResources().getColor(R.color.color_4d4d4d));
            mSettingDescription.setTextColor(getResources().getColor(R.color.color_999999));
        } else {
            int disabledTextColor = getResources().getColor(R.color.color_999999);
            mSettingTitle.setTextColor(disabledTextColor);
            mSettingDescription.setTextColor(disabledTextColor);
        }
    }
}
