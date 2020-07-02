package me.ixk.framework.middleware;

import java.util.List;
import javax.servlet.http.Cookie;
import me.ixk.framework.annotations.GlobalMiddleware;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.facades.Crypt;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SetCookie;

@GlobalMiddleware
@Order(Order.HIGHEST_PRECEDENCE + 1)
public class EncryptCookies implements Middleware {

    @Override
    public Response handle(Request request, Runner next) {
        return this.encrypt(next.handle(this.decrypt(request)));
    }

    protected Request decrypt(Request request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String decrypt = Crypt.decrypt(cookie.getValue());
            if (decrypt != null) {
                cookie.setValue(decrypt);
            }
        }
        return request;
    }

    protected Response encrypt(Response response) {
        List<SetCookie> cookies = response.getCookies();
        for (SetCookie cookie : cookies) {
            if (cookie.isEncrypt()) {
                cookie.setValue(Crypt.encrypt(cookie.getValue()));
            }
        }
        return response;
    }
}
