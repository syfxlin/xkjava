/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.ixk.app.entity.Users;
import me.ixk.app.entity.Visitors;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.app.service.impl.VisitorsServiceImpl;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.database.Transactional;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.GetMapping;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.ViewResult;

@Controller
public class CountController {

    @Autowired
    VisitorsServiceImpl visitorsService;

    @Autowired
    UsersServiceImpl usersService;

    @GetMapping("/count")
    @Transactional
    public ViewResult index() {
        final Visitors visitorsOnline = visitorsService.getById(1);
        final Visitors visitorsHistory = visitorsService.getById(2);
        final Visitors visitorsLogin = visitorsService.getById(3);
        final Map<String, Object> objectMap = new ConcurrentHashMap<>();
        objectMap.put("countOnline", visitorsOnline.getCounts());
        objectMap.put("countHistory", visitorsHistory.getCounts());
        final String users = visitorsLogin.getUsers();
        final List<Users> usersList = users == null || users.isEmpty()
            ? new ArrayList<>()
            : Arrays
                .stream(users.split(","))
                .map(Long::parseLong)
                .map(usersService::getById)
                .collect(Collectors.toList());
        objectMap.put("usersList", usersList);
        return Result.view("count/index", objectMap);
    }
}
