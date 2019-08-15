package com.juhao.iot.scene;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.BaseActivity;
import com.juhao.iot.R;

public class IotSceneAddActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        fullScreen(this);
    setContentView(R.layout.activity_iot_scene);
    TextView tv_save=findViewById(R.id.tv_save);
    TextView tv_name=findViewById(R.id.tv_name);
    ImageView iv_img=findViewById(R.id.iv_img);
    ImageView iv_add=findViewById(R.id.iv_add);
    ListView lv_action=findViewById(R.id.lv_action);
    tv_name.setOnClickListener(this);
    tv_save.setOnClickListener(this);
    iv_img.setOnClickListener(this);
    iv_add.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.tv_save:
            break;
        case R.id.tv_name:
            break;
        case R.id.iv_img:
            break;
        case R.id.iv_add:
            startActivity(new Intent(this,IotSceneActionAddActivity.class));
            break;
    }
    }
}
