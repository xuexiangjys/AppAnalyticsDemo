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
import com.xuexiang.appanalyticsdemo.http.callback.DialogJsonCallback;

import java.util.HashMap;
import java.util.Map;

public class LoginTask {
	private static final String URL_LOGIN = "demo/sdkLogin";

	private static LoginTask instance;

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

	public void login(VerifyResult verifyResult, DialogJsonCallback<LoginResult> callback) {
		Map<String, String> values = new HashMap<>();
		if (verifyResult != null) {
			values.put("opToken", verifyResult.getOpToken());
			values.put("operator", verifyResult.getOperator());
			values.put("phoneOperator", verifyResult.getOperator());
			values.put("token", verifyResult.getToken());
			values.put("md5", DeviceHelper.getInstance(MobSDK.getContext()).getSignMD5());
		}
		OkGo.<LoginResult>post(ServerConfig.getServerUrl() + URL_LOGIN)
				.params(values)
				.execute(callback);
	}
}
