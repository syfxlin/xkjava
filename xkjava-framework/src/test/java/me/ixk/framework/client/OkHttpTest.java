/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.client;

import java.io.IOException;
import me.ixk.framework.test.XkJavaTest;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

/**
 * OkHttp 测试
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 1:30
 */
@XkJavaTest
class OkHttpTest {

    @Test
    void test() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .get()
            .url("http://localhost:8080/welcome")
            .build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
    }
}
