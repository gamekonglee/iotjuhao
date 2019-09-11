package com.juhao.iot.ui;

import android.view.View;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.iot.R;
import com.juhao.iot.UIUtils;
import com.view.MyToast;

public class SettingActivity extends BaseActivity {

    private TextView tv_account;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
    setContentView(R.layout.activity_setting);
        tv_account = findViewById(R.id.tv_account);

    View tv_logout=findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showSingleWordDialog(SettingActivity.this, getString(R.string.str_sure_logout), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginBusiness.logout(new ILogoutCallback() {
                            @Override
                            public void onLogoutSuccess() {
                                MyToast.show(SettingActivity.this,getString(R.string.str_logout_success));
                                SettingActivity.this.finish();
                                DemoApplication.activityList.get(0).finish();
                                System.exit(0);
//                                Log.i(TAG,"登出成功");
                            }

                            @Override
                            public void onLogoutFailed(int code, String error) {
//                                Log.i(TAG,"登出失败");
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void initData() {

    }
}
