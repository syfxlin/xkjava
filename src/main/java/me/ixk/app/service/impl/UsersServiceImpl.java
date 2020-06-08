package me.ixk.app.service.impl;

import me.ixk.app.entity.Users;
import me.ixk.app.mapper.UsersMapper;
import me.ixk.app.service.IUsersService;
import me.ixk.framework.service.ServiceImpl;

public class UsersServiceImpl
    extends ServiceImpl<UsersMapper, Users>
    implements IUsersService {}
