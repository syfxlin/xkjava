package me.ixk.app.controllers.Auth;

import me.ixk.app.entity.LoginUser;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.facades.Auth;
import me.ixk.framework.facades.Resp;
import me.ixk.framework.facades.View;
import me.ixk.framework.http.Response;
import me.ixk.framework.utils.ReflectUtils;
import me.ixk.framework.view.ViewResult;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ViewResult index() {
        return View.make("auth/login");
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
}
