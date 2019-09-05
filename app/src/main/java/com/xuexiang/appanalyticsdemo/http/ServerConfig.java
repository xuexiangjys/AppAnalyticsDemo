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

import android.text.TextUtils;

import com.xuexiang.appanalyticsdemo.BuildConfig;

public class ServerConfig {
    private static final String PROTOCOL = "http://";
    private static final String SERVER_URL_DEBUG = "10.18.97.63:22345";
    private static final String SERVER_URL_RELEASE = "demo.verify.mob.com";

    public static String getServerUrl() {
        if (BuildConfig.DEBUG) {
            return checkSuffix(PROTOCOL + SERVER_URL_DEBUG);
        } else {
            return checkSuffix(PROTOCOL + SERVER_URL_RELEASE);
        }
    }

    private static String checkSuffix(String url) {
        if (!TextUtils.isEmpty(url) && !url.endsWith("/")) {
            url += "/";
        }
        return url;
    }
}
