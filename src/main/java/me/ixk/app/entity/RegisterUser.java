/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.entity;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.ioc.Application;

@Data
@EqualsAndHashCode
public class RegisterUser {
    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @Size(min = 1, max = 50)
    private String nickname;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6, max = 50)
    private String password;

    @NotNull
    @Size(min = 6, max = 50)
    private String passwordConfirmed;

    @AssertTrue(message = "两次密码不一致")
    private boolean isPasswordEquals() {
        return this.password != null && password.equals(this.passwordConfirmed);
    }

    @AssertTrue(message = "用户名已存在")
    private boolean isUnique() {
        if (this.username == null) {
            return false;
        }
        UsersServiceImpl usersService = Application
            .get()
            .make(UsersServiceImpl.class);
        return usersService.query().eq("username", this.username).count() == 0;
    }
}
