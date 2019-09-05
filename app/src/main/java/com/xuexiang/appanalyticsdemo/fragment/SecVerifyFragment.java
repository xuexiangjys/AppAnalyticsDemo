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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.mob.secverify.OperationCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.exception.VerifyException;
import com.mob.secverify.ui.component.CommonProgressDialog;
import com.xuexiang.appanalyticsdemo.R;
import com.xuexiang.appanalyticsdemo.http.LoginTask;
import com.xuexiang.appanalyticsdemo.http.api.MobResult;
import com.xuexiang.appanalyticsdemo.http.callback.TipJsonCallback;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.common.logger.Logger;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.system.PhoneUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * @author xuexiang
 * @since 2019-09-05 14:23
 */
@Page(name = "手机秒验")
public class SecVerifyFragment extends XPageFragment {

    @BindView(R.id.tv_info)
    TextView tvInfo;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sec_verify;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListeners() {
        preVerify();
    }

    @SingleClick
    @OnClick({R.id.btn_verify, R.id.btn_get_phone_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_phone_info:
                getPhoneInfo();
                break;
            case R.id.btn_verify:
                verify();
                break;
            default:
                break;
        }
    }

    @Permission({READ_PHONE_STATE})
    private void verify() {
        CommonProgressDialog.showProgressDialog(getContext());
        SecVerify.verify(new VerifyCallback() {
            @Override
            public void onOtherLogin() {
                // 用户点击“其他登录方式”，处理自己的逻辑
                CommonProgressDialog.dismissProgressDialog();
                ToastUtils.toast("其他方式登录");
            }
            @Override
            public void onUserCanceled() {
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
                CommonProgressDialog.dismissProgressDialog();
                ToastUtils.toast("用户取消登录");
            }
            @Override
            public void onComplete(VerifyResult data) {
                //成功之后不会自动关闭授权页面，需要手动关闭
                SecVerify.finishOAuthPage();
                CommonProgressDialog.dismissProgressDialog();
                if (data != null) {
                    Log.e("xuexiang", "VerifyResult:" + data.toJSONString());
                    // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                    LoginTask.getInstance().login(data, new TipJsonCallback<MobResult>() {
                        /**
                         * 对返回数据进行操作的回调， UI线程
                         *
                         * @param response
                         */
                        @Override
                        public void onSuccess(Response<MobResult> response) {
                            ToastUtils.toast("秒验成功！");
                            tvInfo.setText(JsonUtil.toJson(response.body().getLoginResult()));
                        }

                        @Override
                        public void onError(Response<MobResult> response) {
                            super.onError(response);
                            Logger.eTag("xuexiang", response.getException());
                            preVerify();
                        }
                    });
                }
            }

            @Override
            public void onFailure(VerifyException e) {
                // 登录失败
                //失败之后不会自动关闭授权页面，需要手动关闭
                SecVerify.finishOAuthPage();
                CommonProgressDialog.dismissProgressDialog();
                // 错误码
                String msg = getErrorMessage(e);
                Log.e("xuexiang", msg);
                ToastUtils.toast(msg);
            }
        });
    }



    @SuppressLint("MissingPermission")
    @Permission({READ_PHONE_STATE})
    private void getPhoneInfo() {
        tvInfo.setText(PhoneUtils.getPhoneInfo());
    }

    /**
     * 预登录
     * <p>
     * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
     */
    @Permission({READ_PHONE_STATE})
    private void preVerify() {
        SecVerify.preVerify(new OperationCallback() {
            @Override
            public void onComplete(Object data) {
                ToastUtils.toast("预登录成功");
            }

            @Override
            public void onFailure(VerifyException e) {
                String msg = getErrorMessage(e);
                Log.e("xuexiang", msg);
                ToastUtils.toast(msg);
            }
        });
    }

    private String getErrorMessage(VerifyException e) {
        // 错误码
        int errCode = e.getCode();
        // 错误信息
        String errMsg = e.getMessage();
        // 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
        Throwable t = e.getCause();
        String errDetail = null;
        if (t != null) {
            errDetail = t.getMessage();
        }

        String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
        if (!TextUtils.isEmpty(errDetail)) {
            msg += "\n详细信息: " + errDetail;
        }
        return msg;
    }



}
