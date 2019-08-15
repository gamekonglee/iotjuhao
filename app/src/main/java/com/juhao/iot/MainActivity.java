package com.juhao.iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenAccountUIService openAccountUIService=new OpenAccountUIServiceImpl();
        openAccountUIService.showLogin(this,MyLoginActivity.class, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
//        LoginBusiness.login(new ILoginCallback() {
//            @Override
//            public void onLoginSuccess() {
//                Log.i(TAG,"登录成功");
//            }
//
//            @Override
//            public void onLoginFailed(int code, String error) {
//                Log.i(TAG,"登录失败");
//            }
//        });
    }
}
