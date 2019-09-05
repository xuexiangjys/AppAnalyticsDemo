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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.model.Response;
import com.mob.secverify.CustomUIRegister;
import com.mob.secverify.CustomViewClickListener;
import com.mob.secverify.OperationCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.datatype.LoginResult;
import com.mob.secverify.datatype.UiSettings;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.exception.VerifyException;
import com.mob.secverify.ui.component.CommonProgressDialog;
import com.mob.tools.utils.ResHelper;
import com.xuexiang.appanalyticsdemo.R;
import com.xuexiang.appanalyticsdemo.http.LoginTask;
import com.xuexiang.appanalyticsdemo.http.callback.DialogJsonCallback;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.common.logger.Logger;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.system.PhoneUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.ArrayList;
import java.util.List;

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
        addCustomView();
        customizeUi();

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
                    LoginTask.getInstance().login(data, new DialogJsonCallback<LoginResult>() {
                        /**
                         * 对返回数据进行操作的回调， UI线程
                         *
                         * @param response
                         */
                        @Override
                        public void onSuccess(Response<LoginResult> response) {
                            ToastUtils.toast("秒验成功！");
                            tvInfo.setText(response.body().toJSONString());
                        }

                        @Override
                        public void onError(Response<LoginResult> response) {
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
                Log.e("xuexiang", "preVerify:" + JsonUtil.toJson(data));
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


    /**
     * 添加自定义view
     */
    private void addCustomView() {
        // view仅用于使btn1和btn2水平居中显示
        View view = new View(getContext());
        view.setId(R.id.customized_view_id);
        view.setBackground(getResources().getDrawable(R.drawable.sec_verify_background));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = ResHelper.dipToPx(getContext(),260);
        view.setLayoutParams(params);

        // 自定义按钮1
        ImageView btn1 = new ImageView(getContext());
        btn1.setId(R.id.customized_btn_id_1);
        btn1.setImageDrawable(getResources().getDrawable(R.drawable.sec_verify_demo_close));
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.topMargin = ResHelper.dipToPx(getContext(),280);
        params1.leftMargin = ResHelper.dipToPx(getContext(),15);
        btn1.setLayoutParams(params1);

        List<View> views = new ArrayList<>();
        views.add(view);
        views.add(btn1);

        CustomUIRegister.addCustomizedUi(views, new CustomViewClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                String msg = "";
                if (id == R.id.customized_btn_id_1) {
                    msg = "用户取消登录";
                    // 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
                    SecVerify.finishOAuthPage();
                    CommonProgressDialog.dismissProgressDialog();
                } else if (id == R.id.customized_view_id) {
                    return;
                }
                ToastUtils.toast(msg);
            }
        });
    }

    /**
     * 自定义授权页面UI样式
     */
    private void customizeUi() {
        UiSettings uiSettings = new UiSettings.Builder()
                /** 标题栏 */
                // 标题栏背景色资源ID
                .setNavColorId(R.color.sec_verify_demo_text_color_common_white)
                //标题栏是否透明
                .setNavTransparent(false)
                //标题栏是否隐藏
                .setNavHidden(true)
                //设置背景图片
                .setBackgroundImgId(R.drawable.sec_verify_background_demo)
                //设置背景是否点击关闭页面
                .setBackgroundClickClose(true)
                // 标题栏标题文字资源ID
                .setNavTextId(R.string.sec_verify_demo_verify)
                // 标题栏文字颜色资源ID
                .setNavTextColorId(R.color.sec_verify_demo_text_color_common_black)
                // 标题栏左侧关闭按钮图片资源ID
                .setNavCloseImgId(R.drawable.sec_verify_demo_close)
                //标题栏返回按钮是否隐藏
                .setNavCloseImgHidden(false)
                /** Logo */
                // Logo图片资源ID，默认使用应用图标
                .setLogoImgId(R.mipmap.ic_launcher)
                //logo是否隐藏
                .setLogoHidden(false)
//				//logo宽度
                .setLogoWidth(R.dimen.sec_verify_demo_logo_width)
                //logo高度
                .setLogoHeight(R.dimen.sec_verify_demo_logo_height)
                //logo x轴偏移量
                .setLogoOffsetX(R.dimen.sec_verify_demo_logo_offset_x)
                //logo y轴偏移量
                .setLogoOffsetY(R.dimen.sec_verify_demo_logo_offset_y)
                /** 手机号 */
                // 脱敏手机号字体颜色资源ID
                .setNumberColorId(R.color.sec_verify_demo_text_color_common_black)
                // 脱敏手机号字体大小资源ID
                .setNumberSizeId(R.dimen.sec_verify_demo_text_size_m)
                //脱敏手机号 x轴偏移量
                .setNumberOffsetX(R.dimen.sec_verify_demo_number_field_offset_x)
                //脱敏手机号 y轴偏移量
                .setNumberOffsetY(R.dimen.sec_verify_demo_number_field_offset_y)
                /** 切换帐号 */
                // 切换账号字体颜色资源ID
                .setSwitchAccColorId(R.color.sec_verify_demo_text_color_blue)
                //切换账号 字体大小
                .setSwitchAccTextSize(R.dimen.sec_verify_demo_text_size_s)
                // 切换账号是否显示，默认显示
                .setSwitchAccHidden(false)
                //切换账号 x轴偏移量
                .setSwitchAccOffsetX(R.dimen.sec_verify_demo_switch_acc_offset_x)
                //切换账号 y轴偏移量
                .setSwitchAccOffsetY(R.dimen.sec_verify_demo_switch_acc_offset_y)

                /** 登录按钮 */
                // 登录按钮背景图资源ID，建议使用shape
                .setLoginBtnImgId(R.drawable.sec_verify_demo_shape_rectangle)
                // 登录按钮文字资源ID
                .setLoginBtnTextId(R.string.sec_verify_demo_login)
                // 登录按钮字体颜色资源ID
                .setLoginBtnTextColorId(R.color.sec_verify_demo_text_color_common_white)
                //登录按钮字体大小
                .setLoginBtnTextSize(R.dimen.sec_verify_demo_text_size_s)
                //登录按钮 width
//				.setLoginBtnWidth(R.dimen.sec_verify_demo_login_btn_width)
                //登录按钮 height
//				.setLoginBtnHeight(R.dimen.sec_verify_demo_login_btn_height)
                //登录按钮 x轴偏移
//				.setLoginBtnOffsetX(R.dimen.sec_verify_demo_login_btn_offset_x)
                //登录按钮 y轴偏移
                .setLoginBtnOffsetY(R.dimen.sec_verify_demo_login_btn_offset_y)
                /** 隐私协议 */
                //是否隐藏复选框(设置此属性true时setCheckboxDefaultState不会生效)
                .setCheckboxHidden(false)
                // 隐私协议复选框背景图资源ID，建议使用selector
                .setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
                // 隐私协议复选框默认状态，默认为“选中”
                .setCheckboxDefaultState(true)
                // 隐私协议字体颜色资源ID（自定义隐私协议的字体颜色也受该值影响）
                .setAgreementColorId(R.color.sec_verify_demo_main_color)
                // 自定义隐私协议一文字资源ID
                .setCusAgreementNameId1(R.string.sec_verify_demo_customize_agreement_name_1)
                // 自定义隐私协议一URL
                .setCusAgreementUrl1("http://www.baidu.com")
//				自定义隐私协议一颜色
                .setCusAgreementColor1(R.color.sec_verify_demo_main_color)
                // 自定义隐私协议二文字资源ID
                .setCusAgreementNameId2(R.string.sec_verify_demo_customize_agreement_name_2)
                // 自定义隐私协议二URL
                .setCusAgreementUrl2("https://www.jianshu.com")
                //自定义隐私协议二颜色
                .setCusAgreementColor2(R.color.sec_verify_demo_main_color)
                //隐私协议是否左对齐，默认居中
                .setAgreementGravityLeft(true)
                //隐私协议其他文字颜色
                .setAgreementBaseTextColorId(R.color.sec_verify_demo_text_color_blue)
                //隐私协议 x轴左偏移量
                .setAgreementOffsetX(R.dimen.sec_verify_demo_agreement_offset_x)
                //隐私协议 x轴右偏移量
                .setAgreementOffsetRightX(R.dimen.sec_verify_demo_agreement_offset_x)
                //隐私协议 y轴偏移量
                .setAgreementOffsetY(R.dimen.sec_verify_demo_agreement_offset_y)
                //隐私协议 底部y轴偏移量
//				.setAgreementOffsetBottomY(R.dimen.sec_verify_demo_agreement_offset_bottom_y)
                /** slogan */
                //slogan文字大小
                .setSloganTextSize(R.dimen.sec_verify_demo_text_size_xs)
                //slogan文字颜色
                .setSloganTextColor(R.color.sec_verify_demo_main_color)
                //slogan x轴偏移量
//				.setSloganOffsetX(R.dimen.sec_verify_demo_slogan_offset_x)
                //slogan y轴偏移量
                .setSloganOffsetY(R.dimen.sec_verify_demo_slogan_offset_y)
                //slogan 底部y轴偏移量(设置此属性时，setSloganOffsetY不生效)
                .setSloganOffsetBottomY(R.dimen.sec_verify_demo_slogan_offset_bottom_y)
                .build();
        SecVerify.setUiSettings(uiSettings);
    }

}
