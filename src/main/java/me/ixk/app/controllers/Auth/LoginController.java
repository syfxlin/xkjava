package me.ixk.app.controllers.Auth;

import me.ixk.app.entity.LoginUser;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.facades.Auth;
import me.ixk.framework.facades.Resp;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.result.ViewResult;
import me.ixk.framework.utils.ReflectUtils;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ViewResult index() {
        return ViewResult.make("auth/login");
    }

    @PostMapping("/login")
    public Response login(LoginUser user) {
        ReflectUtils.getProxyTarget(user);
        me.ixk.framework.kernel.Auth.Result result = Auth.login(user);
        if (result.isOk()) {
            return Resp.redirect("/free-marker");
        }
        return Resp.text(result.getResult().getErrors().toString());
    }

    @GetMapping("/logout")
    public ViewResult logout() {
        Auth.logout();
        return ViewResult.make("auth/login");
    }
}
