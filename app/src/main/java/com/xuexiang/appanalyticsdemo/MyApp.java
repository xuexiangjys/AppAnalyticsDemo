/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.appanalyticsdemo;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.xuexiang.xaop.XAOP;
import com.xuexiang.xaop.util.PermissionUtils;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.system.DeviceUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.util.List;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:12
 */
public class MyApp extends Application {

    private static final String UMENG_APP_ID = "5d01b5543fc195f587000182";
    private static final String UMENG_MESSAGE_SECRET = "07c297ead965629be9a2d4bc74846019";

    private static final String BUGLY_APP_ID = "10b84c5e6f";

    private static String CHANNEL_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        CHANNEL_ID = WalleChannelReader.getChannel(this);
        Log.e("xuexiang", "CHANNEL_ID:" + CHANNEL_ID);

        initLibs();

        initUMeng();

        initBugly();
    }


    /**
     * 初始化基础库
     */
    private void initLibs() {
        XUtil.init(this);
        XUtil.debug(BuildConfig.DEBUG);

        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() { //页面注册
            @Override
            public List<PageInfo> registerPages(Context context) {
                return AppPageConfig.getInstance().getPages(); //自动注册页面
            }
        }).debug("PageLog").enableWatcher(true).init(this);

        XAOP.init(this); //初始化插件
        XAOP.debug(BuildConfig.DEBUG); //日志打印切片开启
        //设置动态申请权限切片 申请权限被拒绝的事件响应监听
        XAOP.setOnPermissionDeniedListener(new PermissionUtils.OnPermissionDeniedListener() {
            @Override
            public void onDenied(List<String> permissionsDenied) {
                ToastUtils.toast("权限申请被拒绝:" + StringUtils.listToString(permissionsDenied, ","));
            }

        });
    }

    /**
     * 初始化UmengSDK
     */
    private void initUMeng() {
        long start = System.currentTimeMillis();
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(true);
        //初始化组件化基础库, 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，UMConfigure.init调用中appkey和channel参数请置为null）。
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey（必须填）；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        UMConfigure.init(this, UMENG_APP_ID, CHANNEL_ID, UMConfigure.DEVICE_TYPE_PHONE, UMENG_MESSAGE_SECRET);
        initUMengPush();
        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        long end = System.currentTimeMillis();
        Log.e("initUMeng time--->", end - start + "ms");
    }

    /**
     * 初始化友盟推送
     */
    private void initUMengPush() {
        //获取消息推送代理示例
        PushAgent pushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志T
                ToastUtils.toast("推送服务连接成功！");
                Log.i("xuexiang", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("xuexiang", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.layout_notification_view);
                        notificationView.setTextViewText(R.id.notification_title, msg.title);
                        notificationView.setTextViewText(R.id.notification_text, msg.text);
                        notificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        notificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(notificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        pushAgent.setMessageHandler(messageHandler);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                ToastUtils.toast("点击推送：" + msg.title);
                Log.e("xuexiang", "click");
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //小米推送
        MiPushRegistar.register(this, "2882303761518028967", "5661802845967");
        //华为推送
        HuaWeiRegister.register(this);

    }

    private void initBugly() {
        long start = System.currentTimeMillis();
        BuglyStrategy strategy = new BuglyStrategy();
        strategy.setEnableANRCrashMonitor(true)
                .setEnableNativeCrashMonitor(true)
                .setUploadProcess(true)
                .setAppChannel(CHANNEL_ID)
                .setDeviceID(DeviceUtils.getAndroidID())
                .setRecordUserInfoOnceADay(true);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        Bugly.init(this, BUGLY_APP_ID, true, strategy);
        long end = System.currentTimeMillis();
        Log.e("initBugly time--->", end - start + "ms");
    }
}
