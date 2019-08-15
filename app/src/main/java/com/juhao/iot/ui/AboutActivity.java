package com.juhao.iot.ui;

import android.widget.TextView;

import com.BaseActivity;
import com.juhao.iot.R;
import com.juhao.iot.UIUtils;

public class AboutActivity extends BaseActivity {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_about);
        TextView tv_version=findViewById(R.id.tv_version);
        tv_version.setText("钜豪智慧家庭v"+UIUtils.getVerName(this));
    }

    @Override
    protected void initData() {

    }
}
