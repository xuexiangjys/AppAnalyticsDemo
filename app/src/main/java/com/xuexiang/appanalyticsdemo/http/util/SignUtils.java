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

package com.xuexiang.appanalyticsdemo.http.util;

import com.xuexiang.xutil.common.StringUtils;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xuexiang
 * @since 2019-09-05 21:58
 */
public final class SignUtils {

    private static String charset = "utf8";

    private SignUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getSign(Map<String, Object> data, String secret) {
        if (data == null) {
            return null;
        }
        //排序参数
        Map<String, Object> mappingList = new TreeMap<>(data);
        StringBuilder plainText = new StringBuilder();
        for (Map.Entry<String, Object> entry : mappingList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            if (!"sign".equals(key) && !StringUtils.isEmpty(value)) {
                plainText.append(String.format("%s=%s&", key, value));
            }
        }
        String substring = plainText.substring(0, plainText.length() - 1);
        return MD5Encode(substring + secret, charset);
    }


    private static String byteArrayToHexString(byte b[]) {
        StringBuilder resultSb = new StringBuilder();
        for (byte b1 : b) {
            resultSb.append(byteToHexString(b1));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetName) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetName == null || "".equals(charsetName)) {
                result = byteArrayToHexString(md.digest(origin.getBytes()));
            } else {
                result = byteArrayToHexString(md.digest(origin.getBytes(charsetName)));
            }
        } catch (Exception exception) {
        }
        return result;
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
}
