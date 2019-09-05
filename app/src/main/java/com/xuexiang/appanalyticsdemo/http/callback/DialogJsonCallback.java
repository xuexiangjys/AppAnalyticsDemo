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

package com.xuexiang.appanalyticsdemo.http.callback;

import android.content.Context;

import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.mob.secverify.ui.component.CommonProgressDialog;
import com.xuexiang.xutil.tip.ToastUtils;

/**
 * 带loading弹窗的回调
 *
 * @author xuexiang
 * @since 2019-07-04 23:58
 */
public abstract class DialogJsonCallback<T> extends JsonCallback<T> {

    private Context mContext;

    public DialogJsonCallback() {

    }

    public DialogJsonCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        CommonProgressDialog.showProgressDialog(mContext);
    }

    @Override
    public void onError(Response<T> response) {
        super.onError(response);
        ToastUtils.toast(response.message());
    }

    @Override
    public void onFinish() {
        CommonProgressDialog.dismissProgressDialog();
    }
}
