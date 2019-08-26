package com.juhao.iot.scene;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.BaseActivity;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.api.TmpInitConfig;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.alink.linksdk.tmp.listener.IDevListener;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.bean.AccountDevDTO;
import com.google.gson.Gson;
import com.juhao.iot.MyLoginActivity;
import com.juhao.iot.R;
import com.juhao.iot.UIUtils;
import com.juhao.iot.adapter.BaseAdapterHelper;
import com.juhao.iot.adapter.QuickAdapter;
import com.util.Constance;
import com.util.LogUtils;
import com.view.EndOfListView;
import com.view.MyToast;
import com.view.PMSwipeRefreshLayout;
import com.view.TextViewPlus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotSceneDeviceAddActivity extends BaseActivity {

    private EndOfListView lv_devices;
    private QuickAdapter adapter;
    private List<AccountDevDTO> accountDevDTOS;
    private ArrayList<JSONObject> mDeviceList;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                tv_none_devices.setVisibility(View.VISIBLE);
                lv_devices.setVisibility(View.GONE);
            }else if(msg.what==0){
                tv_none_devices.setVisibility(View.GONE);
                lv_devices.setVisibility(View.VISIBLE);
                adapter.replaceAll(accountDevDTOS);
                if(accountDevDTOS==null||accountDevDTOS.size()==0)return;
                for(int i=0;i<accountDevDTOS.size();i++){
                    if(accountDevDTOS.get(i).getName().contains("灯")){
                        iotId = accountDevDTOS.get(i).getIotId();
                        break;
                    }
                }
                if(TextUtils.isEmpty(iotId))return;
//                ll_cmd.setVisibility(View.VISIBLE);
                TmpSdk.init(IotSceneDeviceAddActivity.this, new TmpInitConfig(TmpInitConfig.ONLINE));
                TmpSdk.getDeviceManager().discoverDevices(null, 5000, new IDevListener() {
                    @Override
                    public void onSuccess(Object o, OutputParams outputParams) {

                    }

                    @Override
                    public void onFail(Object o, ErrorInfo errorInfo) {

                    }
                });

                final PanelDevice panelDevice = new PanelDevice(iotId);
                panelDevice.init(IotSceneDeviceAddActivity.this, new IPanelCallback() {
                    @Override
                    public void onComplete(boolean b, Object o) {
                        Log.e("panelDevice","onComplete:"+b+o);
                        panelDevice.getStatus(new IPanelCallback() {
                            @Override
                            public void onComplete(boolean bSuc, Object o) {
                                Log.e("getStatus","onComplete:"+bSuc);
                                try {
                                    JSONObject data = new JSONObject((String)o);
                                    status = data.getJSONObject(Constance.data).getInt(Constance.status);
                                    if(status ==1){
                                        return;
                                    }
//                                    Log.e("panelDevice_status", status +"");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }else if(msg.what==3){
//                MyToast.show(getActivity(),"设置成功");
            }
        }
    };
    private TextViewPlus tv_none_devices;
    private String iotId;
    private int status;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_iot_device_add);
        lv_devices = findViewById(R.id.lv_devices);
        tv_none_devices = findViewById(R.id.tv_none_devices);
        adapter = new QuickAdapter<AccountDevDTO>(this, R.layout.item_home_dev) {
            @Override
            protected void convert(BaseAdapterHelper helper, AccountDevDTO item) {
                helper.setText(R.id.tv_name,item.getName());
                helper.setText(R.id.tv_scene,"客厅");
                helper.setText(R.id.tv_status,item.getStatus());
                int resId=R.mipmap.home_kg;
                if(item.getName().contains("插座")){
                    resId=R.mipmap.home_cz;
                }else if(item.getName().contains("开关")){
                    resId=R.mipmap.home_kg;
                }else if(item.getName().contains("灯")){
                    resId=R.mipmap.home_zm;
                }
                helper.setImageResource(R.id.iv_img,resId);
            }
        };
        lv_devices.setAdapter(adapter);
        listByAccount();
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String,Object> map=new HashMap<>();
                map.put("iotId",accountDevDTOS.get(i).getIotId()+"");
                IoTRequestBuilder builder=new IoTRequestBuilder()
//                        .setPath("/thing/tsl/get")
                        .setPath("/thing/events/get")
                        .setApiVersion("1.0.2")
                        .setAuthType("iotAuth")
                        .setParams(map);
                IoTRequest request=builder.build();
                IoTAPIClient ioTAPIClient=new IoTAPIClientFactory().getClient();
                ioTAPIClient.send(request, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                        final int code = response.getCode();
                        final String msg = response.getMessage();
                        Object data = response.getData();
                        if (null != data) {
                            LogUtils.logE("tsl/get",data.toString());
                        }
                    }
                });
            }
        });
    }
    private void listByAccount(){
        accountDevDTOS = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listByAccount")
                .setApiVersion("1.0.0")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String msg = response.getMessage();

                Object data = response.getData();
                if (null != data) {
                    if(data instanceof JSONArray){
                        mDeviceList = parseDeviceListFromSever((JSONArray) data);
                        if(mDeviceList ==null|| mDeviceList.size()==0){
                            mHandler.sendEmptyMessage(1);
                            return;
                        }
                        LogUtils.logE("mDevices", mDeviceList.toString());
                        for(int i = 0; i< mDeviceList.size(); i++){
                            try {
                                if(!mDeviceList.get(i).getString(Constance.type).equals("虚拟")){
                                }
                                    accountDevDTOS.add(new Gson().fromJson(mDeviceList.get(i).toString(),AccountDevDTO.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for(int i=0;i<accountDevDTOS.size();i++){
                            for(int j=0;j<accountDevDTOS.size();j++){
                                if(i!=j&&accountDevDTOS.get(i).getIotId().equals(accountDevDTOS.get(j).getIotId())){
                                    accountDevDTOS.remove(j);
                                    if(j!=0)j--;
                                }
                            }
                        }

                        mHandler.sendEmptyMessage(0);
                        String[] pks = {"a1IjeL0MqPS", "a1AzoSi5TMc", "a1nZ7Kq7AG1", "a1XoFUJWkPr"};
//                        if (mDeviceList.size() == 0 || virturlDeviceCount < pks.length - 1){
//                            //注册虚拟设备
//
////                            for (String pk : pks) {
//////                                registerVirtualDevice(pk);
////                            }
//                        }else {
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    initDevicePanel();
//                                }
//                            });
//                        }
                    }
                }

            }
        });
    }
    @Override
    protected void initData() {

    }


    private ArrayList<JSONObject> parseDeviceListFromSever(JSONArray jsonArray) {
        int virturlDeviceCount = 0;
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        ArrayList<String> deviceStrList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject device = new JSONObject();
                device.put("name", jsonObject.getString("productName"));

                String type = "虚拟";
                if ("VIRTUAL".equalsIgnoreCase(jsonObject.getString("thingType"))){
                    type = "虚拟";
                    virturlDeviceCount++;
                }else{
                    type = jsonObject.getString("netType");
                }
                device.put("type", type);
                String statusStr = "离线";
                if (1 == jsonObject.getInt("status")){
                    statusStr = "在线";
                }
                device.put("status", statusStr);
                device.put("productKey", jsonObject.getString("productKey"));
                device.put("iotId", jsonObject.getString("iotId"));
                device.put("deviceName", jsonObject.getString("deviceName"));
                deviceStrList.add(jsonObject.getString("productKey") + jsonObject.getString("deviceName"));
                arrayList.add(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        mBundle.putStringArrayList("deviceList", deviceStrList);
        return arrayList;
    }

}
