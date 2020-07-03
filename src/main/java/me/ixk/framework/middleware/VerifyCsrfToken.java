package me.ixk.framework.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.facades.Crypt;
import me.ixk.framework.facades.Session;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;

@GlobalMiddleware
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class VerifyCsrfToken implements Middleware {
    /**
     * 排除使用 CSRF 的 URL（正则）
     */
    protected String[] except = null;

    @Override
    public Response handle(Request request, Runner next) {
        if (
            this.isReading(request) ||
            this.skipVerify(request) ||
            this.verifyToken(request)
        ) {
            return this.setToken(request, next.handle(request));
        }
        throw new HttpException(
            HttpStatus.REQUEST_EXPIRED,
            "CSRF Token needs to be updated."
        );
    }

    protected String getToken(Request request) {
        JsonNode tokenNode = request.input("_token");
        String token;
        if (tokenNode != null && !tokenNode.isNull()) {
            token = tokenNode.asText();
        } else {
            token = request.header("X-CSRF-TOKEN");
            if (token != null) {
                token = Crypt.decrypt(token);
            }
        }
        return token;
    }

    protected boolean verifyToken(Request request) {
        String token = this.getToken(request);
        String sToken = Session.token();
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
        if (this.except == null) {
            return false;
        }
        for (String pattern : this.except) {
            if (request.pattern(pattern)) {
                return true;
            }
        }
        return false;
    }

    protected Response setToken(Request request, Response response) {
        SetCookie cookie = new SetCookie(
            "XSRF-TOKEN",
            Session.token(),
            2628000
        );
        return response.cookie(cookie);
    }
}
