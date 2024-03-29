/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.exception.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.http.SetCookie;
import me.ixk.framework.util.Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 验证 CSRF Token
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:45
 */
@Order(Order.HIGHEST_PRECEDENCE + 4)
public class VerifyCsrfToken implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(
        VerifyCsrfToken.class
    );

    /**
     * 排除使用 CSRF 的 URL（正则）
     */
    protected final String[] except = new String[] {};
    protected final SessionManager session;
    protected final Crypt crypt;

    public VerifyCsrfToken(SessionManager session, Crypt crypt) {
        this.session = session;
        this.crypt = crypt;
    }

    @Override
    public boolean beforeHandle(Request request, Response response)
        throws Exception {
        if (
            !(
                this.isReading(request) ||
                this.skipVerify(request) ||
                this.verifyToken(request)
            )
        ) {
            log.error("CSRF Token needs to be updated");
            throw new HttpException(
                HttpStatus.REQUEST_EXPIRED,
                "CSRF Token needs to be updated."
            );
        }
        return true;
    }

    @Override
    public Object afterHandle(
        Object returnValue,
        Request request,
        Response response
    ) throws Exception {
        this.setToken(response);
        return returnValue;
    }

    protected String getToken(Request request) {
        JsonNode tokenNode = request.body("_token");
        String token;
        if (tokenNode != null && !tokenNode.isNull()) {
            token = tokenNode.asText();
        } else {
            token = request.header("X-CSRF-TOKEN");
            if (token != null) {
                token = this.crypt.decrypt(token);
            }
        }
        return token;
    }

    protected boolean verifyToken(Request request) {
        String token = this.getToken(request);
        String sToken = this.session.token();
        return sToken != null && sToken.equals(token);
    }

    protected boolean isReading(Request request) {
        switch (request.getMethod()) {
            case "HEAD":
            case "GET":
            case "OPTIONS":
                return true;
            default:
                return false;
        }
    }

    protected boolean skipVerify(Request request) {
        for (String pattern : this.except) {
            if (request.pattern(pattern)) {
                return true;
            }
        }
        return false;
    }

    protected void setToken(Response response) {
        SetCookie cookie = new SetCookie(
            "XSRF-TOKEN",
            this.session.token(),
            2628000
        );
        response.cookie(cookie);
    }
}
