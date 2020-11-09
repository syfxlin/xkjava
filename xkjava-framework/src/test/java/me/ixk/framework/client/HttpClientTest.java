/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.test.ClientResponse;
import me.ixk.framework.test.GetResponse;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * OkHttp 测试
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 1:30
 */
@XkJavaTest
class HttpClientTest {

    @Test
    void parameter(
        @ClientResponse(
            "http://localhost:8080/welcome"
        ) final HttpResponse welcome
    ) {
        assertEquals("welcome", welcome.body());
    }

    @Test
    void header(
        @ClientResponse(
            url = "http://localhost:8080/welcome",
            header = { "Test: test" }
        ) final HttpResponse welcome
    ) {
        assertEquals("test", welcome.body());
    }

    @Test
    void post(
        @ClientResponse(
            url = "http://localhost:8080/post",
            body = "{\"Test\":\"test\"}",
            method = HttpMethod.POST
        ) final HttpResponse post
    ) {
        assertEquals("{\"Test\":\"test\"}", post.body());
    }

    @Test
    void get(
        @GetResponse(
            url = "http://localhost:8080/get",
            param = "key=value"
        ) final HttpResponse get
    ) {
        assertEquals("value", get.body());
    }

    @Test
    void notExecute(
        @GetResponse(url = "http://localhost:8080/welcome") HttpRequest welcome
    ) {
        welcome.execute();
    }
}
