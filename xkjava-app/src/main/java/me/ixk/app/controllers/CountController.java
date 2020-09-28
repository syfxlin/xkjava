/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.app.entity.Visitors;
import me.ixk.app.service.impl.VisitorsServiceImpl;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.Transactional;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.ViewResult;

@Controller
public class CountController {
    @Autowired
    VisitorsServiceImpl visitorsService;

    @GetMapping("/count")
    @Transactional
    public ViewResult index() {
        final Visitors visitorsOnline = visitorsService.getById(1);
        final Visitors visitorsHistory = visitorsService.getById(2);
        final Map<String, Object> objectMap = new ConcurrentHashMap<>();
        objectMap.put("countOnline", visitorsOnline.getCounts());
        objectMap.put("countHistory", visitorsHistory.getCounts());
        return Result.view("count/index", objectMap);
    }
}
