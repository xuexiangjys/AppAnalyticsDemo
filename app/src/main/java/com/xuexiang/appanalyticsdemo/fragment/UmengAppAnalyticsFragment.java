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

package com.xuexiang.appanalyticsdemo.fragment;

import com.umeng.analytics.MobclickAgent;
import com.xuexiang.appanalyticsdemo.entity.PersonInfo;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xutil.system.DeviceUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuexiang
 * @since 2019-06-13 11:22
 */
@Page(name = "友盟App数据统计")
public class UmengAppAnalyticsFragment extends XPageSimpleListFragment {

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("监听自定义事件首次触发");
        lists.add("普通事件【指定事件ID】");
        lists.add("多属性事件【K-V键值对(String)】");
        lists.add("多属性事件【K-V键值对(Object)】");
        lists.add("多属性事件【K-V键值对(String) + 数值型】");
        lists.add("用户登录事件");
        lists.add("用户登出事件");
        lists.add("程序崩溃事件");
        lists.add("程序异常事件");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        Map<String, String> map = new HashMap<>();
        map.put("name", "xuexiang");
        map.put("age", "25岁");
        switch(position) {
            case 0:
                String[] eventList = new String[] {"Event_ID", "Event_K_V_String", "Event_K_V_Object", "Event_K_V_Int_Value"};
                MobclickAgent.setFirstLaunchEvent(getContext(), Arrays.asList(eventList));
                break;
            case 1:
                MobclickAgent.onEvent(getContext(), "Event_ID");
                MobclickAgent.onEvent(getContext(), "Event_ID", "Event Label");
                break;
            case 2:
                MobclickAgent.onEvent(getContext(), "Event_K_V_String", map);
                break;
            case 3:
                Map<String, Object> map1 = new HashMap<>();
                map1.put("name", "xuexiang");
                map1.put("age", 25);
                MobclickAgent.onEventObject(getContext(), "Event_K_V_Object", map1);
                break;
            case 4:
                MobclickAgent.onEventValue(getContext(), "Event_K_V_Int_Value", map,1522);
                break;
            case 5:
                MobclickAgent.onProfileSignIn(DeviceUtils.getAndroidID());
                break;
            case 6:
                MobclickAgent.onProfileSignOff();
                break;
            case 7:
                "123".substring(10);
                break;
            case 8:
                try {
                    int a = 100 / 0;
                } catch (Exception e) {
                    MobclickAgent.reportError(getContext(), e);
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        ToastUtils.toast(getSimpleDataItem(position) + "发送成功！");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
    }



}
