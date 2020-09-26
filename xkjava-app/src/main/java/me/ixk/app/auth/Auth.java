/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.auth;

import static me.ixk.framework.helpers.Facade.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.entity.LoginUser;
import me.ixk.app.entity.RegisterUser;
import me.ixk.app.entity.Users;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.http.SetCookie;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.ValidResult;
import me.ixk.framework.utils.Validation;

public class Auth {
    protected Users user = null;

    protected boolean viaRemember = false;

    protected final UsersServiceImpl usersService = XkJava
        .of()
        .make(UsersServiceImpl.class);

    public Result register(RegisterUser user) {
        ValidResult<RegisterUser> result = Validation.validate(user);
        if (result.isFail()) {
            return new Result(result);
        }
        Users dbUser = Users
            .builder()
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .password(hash().make(user.getPassword()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .status(0)
            .build();
        this.usersService.save(dbUser);
        return new Result(dbUser);
    }

    public Result login(LoginUser user) {
        ValidResult<LoginUser> result = Validation.validate(user);
        if (result.isFail()) {
            return new Result(result);
        }
        return this.attempt(user);
    }

    public Result attempt(LoginUser user) {
        Users dbUser =
            this.usersService.query().eq("username", user.getUsername()).one();
        Map<String, String> errors = new ConcurrentHashMap<>();
        if (dbUser == null) {
            errors.put(
                "account",
                "No \"" + user.getUsername() + "\" users found"
            );
            return new Result(errors);
        }
        if (!hash().check(user.getPassword(), dbUser.getPassword())) {
            errors.put("password", "Account does not match the password.");
            return new Result(errors);
        }
        this.user = dbUser;
        this.updateLogin(this.user, user.isRemember_me());
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
        Long id = session().get(this.getName(), Long.class);
        if (id != null) {
            this.user = this.usersService.getById(id);
        }
        if (this.user == null) {
            javax.servlet.http.Cookie tokenCookie = cookie()
                .get(this.getRememberName());
            if (tokenCookie != null) {
                String[] tokens = tokenCookie.getValue().split("\\|");
                if (tokens.length == 3) {
                    this.user =
                        usersService
                            .query()
                            .eq("id", tokens[0])
                            .eq("remember_token", tokens[1])
                            .eq("password", tokens[2])
                            .one();
                }
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
        session().put(this.getName(), id);
    }

    protected void updateRememberToken(Users user) {
        String token = user.getRememberToken();
        if (token == null || token.length() == 0) {
            token = Util.strRandom(40);
            user.setRememberToken(token);
            this.usersService.updateById(user);
        }
    }

    protected void queueRememberTokenCookie(Users user) {
        SetCookie cookie = new SetCookie(
            this.getRememberName(),
            user.getId() +
            "|" +
            user.getRememberToken() +
            "|" +
            user.getPassword()
        );
        cookie.setHttpOnly(true);
        cookie().forever(cookie);
    }

    protected void clearUserDataFromStorage() {
        session().forget(this.getName());
        if (cookie().get(this.getRememberName()) != null) {
            cookie().forget(this.getRememberName());
        }
    }

    protected String getName() {
        return "login_" + Auth.class.getName();
    }

    protected String getRememberName() {
        return "remember_" + Auth.class.getName().replace(".", "_");
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
        private Map<String, String> errors;
        private Users user;

        public Result(ValidResult<?> validResult) {
            this.errors = validResult.getErrorMessages();
        }

        public Result(Map<String, String> errors) {
            this.errors = errors;
        }

        public Result(Users user) {
            this.user = user;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public void setErrors(Map<String, String> errors) {
            this.errors = errors;
        }

        public Users getUser() {
            return user;
        }

        public void setUser(Users user) {
            this.user = user;
        }

        public boolean isOk() {
            return errors == null || errors.isEmpty();
        }

        public boolean isFail() {
            return !this.isOk();
        }
    }
}
