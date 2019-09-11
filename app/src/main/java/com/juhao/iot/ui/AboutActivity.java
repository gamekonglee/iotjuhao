package com.juhao.iot.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
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
        TextView tv_pingfen=findViewById(R.id.tv_pingfen);
        tv_pingfen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.juhao.iot");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        tv_version.setText(getString(R.string.application_name)+"v"+UIUtils.getVerName(this));
    }

    @Override
    protected void initData() {

    }
}
