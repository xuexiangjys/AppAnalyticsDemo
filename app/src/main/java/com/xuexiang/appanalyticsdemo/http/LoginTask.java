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

package com.xuexiang.appanalyticsdemo.http;

import com.lzy.okgo.OkGo;
import com.mob.MobSDK;
import com.mob.secverify.datatype.LoginResult;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.tools.utils.DeviceHelper;
import com.xuexiang.appanalyticsdemo.http.api.MobResult;
import com.xuexiang.appanalyticsdemo.http.callback.TipJsonCallback;
import com.xuexiang.appanalyticsdemo.http.util.SignUtils;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.net.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 免密服务端登录
 *
 * @author xuexiang
 * @since 2019-09-05 21:56
 */
public class LoginTask {

	private static LoginTask instance;

	private static final String LOGIN_URL = "http://identify.verify.mob.com/auth/auth/sdkClientFreeLogin";
	private static final String APP_KEY = "2c529a6f35c09";
	public static final String APP_SECRET = "c496e66d18d9489e37e59906e5924484";


	private LoginTask() {}

	public static LoginTask getInstance() {
		if (instance == null) {
			synchronized (LoginTask.class) {
				if (instance == null) {
					instance = new LoginTask();
				}
			}
		}
		return instance;
	}

	public void login(VerifyResult verifyResult, TipJsonCallback<MobResult> callback) {
		Map<String, Object> values = new HashMap<>();
		if (verifyResult != null) {
			values.put("appkey", APP_KEY);
			values.put("opToken", verifyResult.getOpToken());
			values.put("operator", verifyResult.getOperator());
			values.put("phoneOperator", verifyResult.getOperator());
			values.put("token", verifyResult.getToken());
			values.put("timestamp", DateUtils.getNowMills());
			values.put("md5", DeviceHelper.getInstance(MobSDK.getContext()).getSignMD5());
		}
		values.put("sign", SignUtils.getSign(values, APP_SECRET));
		OkGo.<MobResult>post(LOGIN_URL)
				.upJson(JsonUtil.toJson(values))
				.execute(callback);
	}


}
