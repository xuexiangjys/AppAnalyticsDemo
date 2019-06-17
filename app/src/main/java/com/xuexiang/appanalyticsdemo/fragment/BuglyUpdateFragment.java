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

import android.annotation.SuppressLint;
import android.content.Intent;

import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.app.AppUtils;
import com.xuexiang.xutil.app.IntentUtils;
import com.xuexiang.xutil.app.PathUtils;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;

/**
 * @author xuexiang
 * @since 2019-06-17 16:09
 */
@Page(name = "版本更新/热更新")
public class BuglyUpdateFragment extends XPageSimpleListFragment {

    private static final int REQUEST_CODE_GET_PATCH_PACKAGE = 120;

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("当前版本:" + AppUtils.getAppVersionName());
        lists.add("版本更新（全量）");
        lists.add("手动热更新");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 1:
                checkUpgrade();
                break;
            case 2:
                choosePatchApk();
                break;
            default:
                break;
        }
    }

    @Permission(STORAGE)
    private void checkUpgrade() {
        Beta.checkUpgrade();
    }


    @Permission(STORAGE)
    private void choosePatchApk() {
        ActivityUtils.startActivityForResult(getActivity(), IntentUtils.getDocumentPickerIntent(IntentUtils.DocumentType.ANY), REQUEST_CODE_GET_PATCH_PACKAGE);
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == REQUEST_CODE_GET_PATCH_PACKAGE) {
            if (data != null) {
                String path = PathUtils.getFilePathByUri(data.getData());
                Beta.applyTinkerPatch(getContext(), path);
            }
        }
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
