package com.juhao.iot.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.BaseActivity;
import com.aliyun.alink.apiclient.IoTApiClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.bean.AccountDevDTO;
import com.juhao.iot.R;
import com.util.Constance;
import com.view.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceShareAddUserActivity extends BaseActivity {

    private AccountDevDTO accountDevDTO;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_device_share_add_user);
        final EditText et_phone=findViewById(R.id.et_phone);
        TextView tv_save=findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=et_phone.getText().toString();
                if(TextUtils.isEmpty(phone)){
                    MyToast.show(DeviceShareAddUserActivity.this,getString(R.string.str_enter_account));
                    return;
                }
                List<String > list=new ArrayList<>();
                list.add(accountDevDTO.getIotId());
                Map<String, Object> map=new HashMap<>();
                map.put("iotIdList",list);
                map.put("accountAttr",phone);
                if(phone.contains("@")){
                    map.put("accountAttrType","EMAIL");
                }else{
                    map.put("accountAttrType","MOBILE");
                    map.put("mobileLocationCode","86");
                }
                IoTRequestBuilder builder=new IoTRequestBuilder()
                        .setPath("/uc/shareDevicesAndScenes")
                        .setApiVersion("1.0.7")
                        .setAuthType("iotAuth")
                        .setParams(map);
                IoTRequest request=builder.build();
                IoTAPIClient ioTAPIClient= new IoTAPIClientFactory().getClient();
                ioTAPIClient.send(request, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                      int code=ioTResponse.getCode();
                      Object data=ioTResponse.getData();
                      if(code==200){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(DeviceShareAddUserActivity.this,getString(R.string.str_share_success));
                                    finish();
                                }
                            });
                      }
                    }
                });



            }
        });

    }

    @Override
    protected void initData() {
        if(getIntent()!=null){
            accountDevDTO = (AccountDevDTO) getIntent().getSerializableExtra(Constance.data);

        }
    }
}
