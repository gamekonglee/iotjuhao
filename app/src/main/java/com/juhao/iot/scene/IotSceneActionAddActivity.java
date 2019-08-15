package com.juhao.iot.scene;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.juhao.iot.R;
import com.view.TextViewPlus;

public class IotSceneActionAddActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_action_add);
        TextView tv_device_add=findViewById(R.id.tv_device_add);
        tv_device_add.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.tv_device_add:
            startActivity(new Intent(this,IotSceneDeviceAddActivity.class));
            break;
    }
    }
}
