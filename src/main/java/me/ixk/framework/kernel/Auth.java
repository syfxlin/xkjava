package me.ixk.framework.kernel;

import java.time.LocalDateTime;
import me.ixk.app.entity.LoginUser;
import me.ixk.app.entity.RegisterUser;
import me.ixk.app.entity.Users;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.facades.Cookie;
import me.ixk.framework.facades.Hash;
import me.ixk.framework.facades.Session;
import me.ixk.framework.http.SetCookie;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.utils.Helper;
import me.ixk.framework.utils.Validation;

public class Auth {
    protected Users user = null;

    protected boolean viaRemember = false;

    protected final UsersServiceImpl usersService = Application
        .get()
        .make(UsersServiceImpl.class);

    public Result register(RegisterUser user) {
        Validation.Result result = Validation.validate(user);
        if (result.isFail()) {
            return new Result(result);
        }
        Users dbUser = Users
            .builder()
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .password(Hash.make(user.getPassword()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .status(0)
            .build();
        this.usersService.save(dbUser);
        return new Result(dbUser);
    }

    public Result login(LoginUser user) {
        Validation.Result result = Validation.validate(user);
        if (result.isFail()) {
            return new Result(result);
        }
        return this.attempt(user);
    }

    public Result attempt(LoginUser user) {
        Users dbUser =
            this.usersService.query().eq("username", user.getUsername()).one();
        Validation.Result result = new Validation.Result();
        if (dbUser == null) {
            result.addError(
                "account",
                "No \"" + user.getUsername() + "\" users found"
            );
            return new Result(result);
        }
        if (!Hash.check(user.getPassword(), dbUser.getPassword())) {
            result.addError("password", "Account does not match the password.");
            return new Result(result);
        }
        this.user = dbUser;
        this.updateLogin(this.user, user.isRememberToken());
        return new Result(dbUser);
    }

    public void logout() {
        this.clearUserDataFromStorage();
        if (this.user != null && this.user.getRememberToken().length() > 0) {
            this.updateRememberToken(this.user);
        }
        this.user = null;
    }

    public Users user() {
        if (this.user != null) {
            return this.user;
        }
        Long id = Session.get(this.getName(), Long.class);
        if (id != null) {
            this.user = this.usersService.getById(id);
        }
        if (this.user == null) {
            javax.servlet.http.Cookie tokenCookie = Cookie.get(
                this.getRememberName()
            );
            if (tokenCookie != null) {
                String[] tokens = tokenCookie.getValue().split("/");
                this.user =
                    usersService
                        .query()
                        .eq("id", tokens[0])
                        .eq("remember_token", tokens[1])
                        .eq("password", tokens[2])
                        .one();
                if (this.user != null) {
                    this.updateSession(this.user.getId());
                    this.viaRemember = true;
                }
            }
        }
        return this.user;
    }

    protected void updateLogin(Users user, boolean remember) {
        this.updateSession(user.getId());
        if (remember) {
            this.updateRememberToken(user);
            this.queueRememberTokenCookie(user);
        }
    }

    protected void updateSession(long id) {
        Session.put(this.getName(), id);
    }

    protected void updateRememberToken(Users user) {
        String token = user.getRememberToken();
        if (token == null || token.length() == 0) {
            token = Helper.strRandom(40);
            user.setRememberToken(token);
            this.usersService.updateById(user);
        }
    }

    protected void queueRememberTokenCookie(Users user) {
        SetCookie cookie = new SetCookie(
            this.getRememberName(),
            user.getId() +
            "/" +
            user.getRememberToken() +
            "/" +
            user.getPassword()
        );
        cookie.setHttpOnly(true);
        Cookie.forever(cookie);
    }

    protected void clearUserDataFromStorage() {
        Session.forget(this.getName());
        if (Cookie.get(this.getRememberName()) != null) {
            Cookie.forget(this.getRememberName());
        }
    }

    protected String getName() {
        return "login_" + Auth.class.getName();
    }

    protected String getRememberName() {
        return "remember_" + Auth.class.getName();
    }

    public boolean check() {
        return this.user() != null;
    }

    public boolean guest() {
        return this.user() == null;
    }

    public boolean viaRemember() {
        return this.viaRemember;
    }

    public static class Result {
        private Validation.Result result;
        private Users user;

        public Result(Validation.Result result) {
            this.result = result;
        }

        public Result(Users user) {
            this.user = user;
        }

        public Validation.Result getResult() {
            return result;
        }

        public void setResult(Validation.Result result) {
            this.result = result;
        }

        public Users getUser() {
            return user;
        }

        public void setUser(Users user) {
            this.user = user;
        }

        public boolean isOk() {
            return result == null || result.isOk();
        }

        public boolean isFail() {
            return !this.isOk();
        }
    }
}
