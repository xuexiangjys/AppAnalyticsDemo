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

import com.tencent.bugly.crashreport.CrashReport;
import com.xuexiang.appanalyticsdemo.jni.NativeApi;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;

import java.util.List;

/**
 * @author xuexiang
 * @since 2019-06-13 14:27
 */
@Page(name = "Bugly崩溃异常上报")
public class BuglyFragment extends XPageSimpleListFragment {

    NativeApi nativeApi;

    @Override
    protected void initArgs() {
        nativeApi = new NativeApi();
    }

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("测试Java Crash");
        lists.add("测试ANR Crash");
        lists.add("测试Native Crash");
        lists.add("异常错误崩溃");
        lists.add("测试错误上报");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        switch(position) {
            case 0:
                nativeApi.testJavaCrash();
                break;
            case 1:
                CrashReport.testANRCrash();
                break;
            case 2:
                nativeApi.testNativeCrash();
                break;
            case 3:
                int a = 1000 / 0;
                break;
            case 4:
                try {
                    int s = 1000 / 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
                break;
            default:
                break;
        }
    }
}
