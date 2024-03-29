package com.aliyun.iot.ilop.demo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import androidx.annotation.RequiresApi;
//import android.support.multidex.MultiDex;

import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDex;

import com.BaseApplication;
import com.aliyun.alink.alirn.RNGlobalConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.alink.linksdk.tools.ThreadTools;
import com.aliyun.alink.page.rn.InitializationHelper;
import com.aliyun.iot.aep.component.router.IUrlHandler;
import com.aliyun.iot.aep.component.scan.ScanManager;
import com.aliyun.iot.aep.sdk.connectchannel.log.ALog;
import com.aliyun.iot.ble.util.Log;
import com.aliyun.iot.breeze.api.Config;
import com.aliyun.iot.breeze.api.Factory;
import com.aliyun.iot.breeze.api.IBreeze;
import com.aliyun.iot.breeze.biz.BuildConfig;
import com.aliyun.iot.ilop.demo.page.scan.BoneMobileScanPlugin;
import com.aliyun.iot.ilop.demo.base.OpenAccountSDKDelegate;
import com.aliyun.iot.aep.routerexternal.RouterExternal;
import com.aliyun.iot.aep.sdk.EnvConfigure;
import com.aliyun.iot.aep.sdk.PushManager;
import com.aliyun.iot.aep.sdk.base.delegate.APIGatewaySDKDelegate;
import com.aliyun.iot.aep.sdk.delegate.DownstreamConnectorSDKDelegate;
import com.aliyun.iot.aep.sdk.delegate.RNContainerComponentDelegate;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.bundle.BundleManager;
import com.aliyun.iot.aep.sdk.framework.bundle.IBundleRegister;
import com.aliyun.iot.aep.sdk.framework.bundle.PageConfigure;
import com.aliyun.iot.ilop.ApplicationHelper;
import com.aliyun.iot.ilop.page.scan.ScanPageInitHelper;
import com.facebook.react.FrescoPackage;
import com.juhao.iot.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wuwang on 2017/10/30.
 */

public class DemoApplication extends BaseApplication {
    public static String app_key="27615405";
    public static String productKey="";
    public static String deviceName="";
    public static String token="";
    private static Application instance;
    public static boolean isShowLogin;


//    public static JSONObject UserInfo;
    public static int unreadMsgCount;
//    private static ArrayList<CommentBean> commentList;
    private static DisplayImageOptions options;
    private static DisplayImageOptions defaultOptions;
    private static ImageLoaderConfiguration config;
    private Context mContext;
    public static List<Activity> activityList = new LinkedList();

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        mContext = getApplicationContext();

        initImageLoader();
//        ImageLoadProxy.initImageLoader(mContext);
//        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush
//        JAnalyticsInterface.init(mContext);
//        JAnalyticsInterface.initCrashHandler(mContext);
//        PgyCrashManager.register(this);
        instance=this;
//         Push SDK 需要在主进程和子进程都初始化
        PushManager.getInstance().init(this);
//         其他 SDK, 仅在 主进程上初始化
        String packageName = this.getPackageName();
        if (!packageName.equals(ThreadTools.getProcessName(this, android.os.Process.myPid()))) {
            return;
        }
        ALog.setLevel(ALog.LEVEL_DEBUG);

        // set env for sdks .begin
        // api gateway
        EnvConfigure.putEnvArg(APIGatewaySDKDelegate.ENV_KEY_API_CLIENT_API_ENV, "RELEASE");
        EnvConfigure.putEnvArg(APIGatewaySDKDelegate.ENV_KEY_API_CLIENT_DEFAULT_HOST, "api.link.aliyun.com");
        // OA
        EnvConfigure.putEnvArg(OpenAccountSDKDelegate.ENV_KEY_OPEN_ACCOUNT_HOST, null);
        // MQTT
        EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_HOST, null);
        EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_AUTO_HOST, "false");
        EnvConfigure.putEnvArg(DownstreamConnectorSDKDelegate.ENV_KEY_MQTT_CHECK_ROOT_CRT, "true");
        // set env for sdks .end
        EnvConfigure.putEnvArg(RNContainerComponentDelegate.KEY_RN_CONTAINER_PLUGIN_ENV, "release");
        EnvConfigure.putEnvArg(EnvConfigure.KEY_LANGUAGE, "zh-CN");

        // the key set from sp that need to be put into AConfigure.envArgs
        HashSet spKeySet = new HashSet();
        EnvConfigure.init(this, spKeySet);
        new ApplicationHelper().onCreate(this);
//        /* 加载Native页面 */
        BundleManager.init(this, new IBundleRegister() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void registerPage(Application application, PageConfigure configure) {
                if (null == configure || null == configure.navigationConfigures)
                    return;

                ArrayList<String> nativeUrls = new ArrayList<>();
                ArrayList<PageConfigure.NavigationConfigure> configures = new ArrayList<>();

                PageConfigure.NavigationConfigure deepCopyItem = null;
                for (PageConfigure.NavigationConfigure item : configure.navigationConfigures) {
                    if (null == item.navigationCode || item.navigationCode.isEmpty() || null == item.navigationIntentUrl || item.navigationIntentUrl.isEmpty())
                        continue;

                    deepCopyItem = new PageConfigure.NavigationConfigure();
                    deepCopyItem.navigationCode = item.navigationCode;
                    deepCopyItem.navigationIntentUrl = item.navigationIntentUrl;
                    deepCopyItem.navigationIntentAction = item.navigationIntentAction;
                    deepCopyItem.navigationIntentCategory = item.navigationIntentCategory;

                    configures.add(deepCopyItem);

                    nativeUrls.add(deepCopyItem.navigationIntentUrl);

//                    com.aliyun.iot.aep.sdk.log.ALog.d("BundleManager", "register-native-page: " + item.navigationCode + ", " + item.navigationIntentUrl);

                    RouterExternal.getInstance().registerNativeCodeUrl(deepCopyItem.navigationCode, deepCopyItem.navigationIntentUrl);
                    RouterExternal.getInstance().registerNativePages(nativeUrls, new NativeUrlHandler(deepCopyItem));
                }
            }
        });
        initMobileConnect();
        initBone();
        IBreeze breeze = Factory.createBreeze(this);
        Config config = new Config.Builder()
                .debug(BuildConfig.DEBUG)
                .log(BuildConfig.DEBUG)
                .logLevel(BuildConfig.DEBUG ? Log.VERBOSE :Log.WARN)
                .build();
        breeze.configure(config);


        // 支持扫码调试
        ScanManager.getInstance().registerPlugin("boneMobile", new BoneMobileScanPlugin());
        //初始化pagescan页面的router配置
        ScanPageInitHelper.initPageScanRouterConfig();
    }

    private void initBone() {
        String serverEnv = "production";//仅支持"production",即生产环境
        String pluginEnv = "release";//仅支持“release”
        String language = "zh-CN";//语言环境，目前支持中文“zh-CN”, 英文"en-US"，法文"fr-FR",德文"de-DE",日文"ja-JP",韩文"ko-KR",西班牙文"es-ES",俄文"ru-RU"，八种语言

        // 初始化 BoneMobile RN 容器
        InitializationHelper.initialize(this, pluginEnv, serverEnv,language);
        // 添加基于 Fresco 的图片组件支持
        RNGlobalConfig.addBizPackage(new FrescoPackage());
    }

    private void initMobileConnect() {
        //打开Log 输出
        ALog.setLevel(ALog.LEVEL_DEBUG);

        MobileConnectConfig config = new MobileConnectConfig();
        // 设置 appKey 和 authCode(必填)
        config.appkey = "{"+app_key+"}";
        config.securityGuardAuthcode = "114d";


        // 设置验证服务器（默认不填，SDK会自动使用“API通道SDK“的Host设定）
//        config.authServer = "";

        // 指定长连接服务器地址。 （默认不填，SDK会使用默认的地址及端口。默认为国内华东节点。）
//        config.channelHost = "{长连接服务器域名}";

        // 开启动态选择Host功能。 (默认false，海外环境建议设置为true。此功能前提为ChannelHost 不特殊指定。）
        config.autoSelectChannelHost = false;

        MobileChannel.getInstance().startConnect(this, config, new IMobileConnectListener() {
            @Override
            public void onConnectStateChange(MobileConnectState state) {
                ALog.d("mobileC","onConnectStateChange(), state = "+state.toString());
            }
        });
    }

    /* methods: AApplication */
//
//    @Override
//    protected void onFirstActivityOnCreated(Activity activity, Bundle savedInstanceState) {
//        super.onFirstActivityOnCreated(activity, savedInstanceState);
//    }
//
//    @Override
//    protected void onFirstActivityOnVisible(Activity activity) {
//        super.onFirstActivityOnVisible(activity);
//    }
//
//    @Override
//    protected void onLastActivityDestroyed(Activity activity) {
//        super.onLastActivityDestroyed(activity);
//    }
//
//    @Override
//    protected void onLastActivityInvisible(Activity activity) {
//        super.onLastActivityInvisible(activity);
//    }

    /**
     * help class
     */
    static final private class NativeUrlHandler implements IUrlHandler {

        private final String TAG = "ApplicationHelper$NativeUrlHandler";

        private final PageConfigure.NavigationConfigure navigationConfigure;

        NativeUrlHandler(PageConfigure.NavigationConfigure configures) {
            this.navigationConfigure = configures;
        }

        @Override
        public void onUrlHandle(Context context, String url, Bundle bundle, boolean startActForResult, int reqCode) {
            com.aliyun.iot.aep.sdk.log.ALog.d(TAG, "onUrlHandle: url: " + url);
            if (null == context || null == url || url.isEmpty())
                return;

            /* prepare the intent */
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));

            if (null != this.navigationConfigure.navigationIntentAction)
                intent.setAction(this.navigationConfigure.navigationIntentAction);
            if (null != this.navigationConfigure.navigationIntentCategory)
                intent.addCategory(this.navigationConfigure.navigationIntentCategory);

            /* start the navigated activity */
            com.aliyun.iot.aep.sdk.log.ALog.d(TAG, "startActivity(): url: " + this.navigationConfigure.navigationIntentUrl + ", startActForResult: " + startActForResult + ", reqCode: " + reqCode);
            this.startActivity(context, intent, bundle, startActForResult, reqCode);
        }

        private void startActivity(Context context, Intent intent, Bundle bundle, boolean startActForResult, int reqCode) {
            if (null == context || null == intent)
                return;


            if (null != bundle) {
                intent.putExtras(bundle);
            }
            /* startActivityForResult() 场景，只能被 Activity 调用 */
            if (startActForResult) {
                if (false == (context instanceof Activity))
                    return;

                ((Activity) context).startActivityForResult(intent, reqCode);

                return;
            }

            /* startActivity 被 Application 调用时的处理 */
            if (context instanceof Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            /* startActivity 被 Activity、Service 调用时的处理 */
            else if (context instanceof Activity || context instanceof Service) {
                context.startActivity(intent);
            }
            /* startActivity 被其他组件调用时的处理 */
            else {
                // 暂不支持
            }
        }
    }

    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public  static DisplayImageOptions getImageLoaderOption() {
       if(defaultOptions==null) {
           defaultOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                   .cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                   .cacheOnDisk(true).build();
       }
        // default
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
// Remove
        if(config==null){
           config = new ImageLoaderConfiguration.Builder(
                    getInstance())
                    .threadPoolSize(4)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .denyCacheImageMultipleSizesInMemory()
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
                    .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                    .memoryCacheSize((int) (2 * 1024 * 1024))
                    .memoryCacheSizePercentage(25)
                    .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(200)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .defaultDisplayImageOptions(defaultOptions).writeDebugLogs() // Remove
                    .build();
        }
// Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        if(options==null){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default) // resource or
                    // drawable
                    .showImageForEmptyUri(R.drawable.bg_default) // resource or
                    // drawable
                    .showImageOnFail(R.drawable.bg_default) // resource or
                    .cacheInMemory(true)// drawable
                    .resetViewBeforeLoading(false) // default
                    .delayBeforeLoading(1000).cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(false) // default
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default
                    .build();
        }
        return options;
    }
    public  static String mCId="";
//
//    public static JSONObject mUserObject;

    public  static String imagePath="";

    public  static File cameraPath;

    public static  boolean isClassify=false;

    public static int mCartCount=0;

    public static int mLightIndex = 0;//点出来的灯的序号

    public  static boolean isGoProgramme=false;

//    public static JSONArray mSelectProducts=new JSONArray();
//    public static JSONArray mSelectScreens=new JSONArray();
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
//
}
