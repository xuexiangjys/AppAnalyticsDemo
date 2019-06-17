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

package com.xuexiang.appanalyticsdemo.push;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.umeng.message.UmengNotifyClickActivity;
import com.xuexiang.appanalyticsdemo.R;

import org.android.agoo.common.AgooConstants;

/**
 * 小米系统推送
 *
 * @author xuexiang
 * @since 2019-06-17 11:18
 */
public class MiPushTestActivity extends UmengNotifyClickActivity {
    private static String TAG = MiPushTestActivity.class.getName();
    private TextView mTvMessage;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mipush);
        mTvMessage = findViewById(R.id.tv_message);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        if (mTvMessage != null) {
            mTvMessage.setText(body);
        }
        Log.i(TAG, body);
    }
}
