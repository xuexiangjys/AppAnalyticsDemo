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

package com.xuexiang.appanalyticsdemo.http.api;

import android.util.Base64;

import com.mob.secverify.datatype.LoginResult;
import com.xuexiang.appanalyticsdemo.BuildConfig;
import com.xuexiang.appanalyticsdemo.http.util.Convert;
import com.xuexiang.appanalyticsdemo.http.util.DES;

import static android.util.Base64.DEFAULT;

/**
 * @author xuexiang
 * @since 2019-09-05 22:25
 */
public class MobResult {


    /**
     * error : null
     * res : QArCscmzM94A4Ey+unhgEQBaBGAPtN66KTxG8ckTgr4fiH1jDGV8jpbIV5GPjupQFDjoWdaoPrs=
     * status : 200
     */

    private Object error;
    private String res;
    private int status;

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public LoginResult getLoginResult() {
        try {
            byte[] decode = DES.decode(Base64.decode(res, DEFAULT), BuildConfig.APP_SECRET.getBytes());
            return Convert.fromJson(new String(decode), LoginResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
