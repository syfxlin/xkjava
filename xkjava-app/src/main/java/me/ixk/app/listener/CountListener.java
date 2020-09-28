/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import me.ixk.app.entity.Visitors;
import me.ixk.app.service.impl.VisitorsServiceImpl;
import me.ixk.framework.ioc.XkJava;

@WebListener
@Slf4j
public class CountListener implements HttpSessionListener {
    final VisitorsServiceImpl visitorsService = XkJava
        .of()
        .make(VisitorsServiceImpl.class);

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        final Visitors visitorsOnline = visitorsService.getById(1);
        visitorsOnline.setCounts(visitorsOnline.getCounts() + 1);
        visitorsService.saveOrUpdate(visitorsOnline);
        final Visitors visitorsHistory = visitorsService.getById(2);
        visitorsHistory.setCounts(visitorsHistory.getCounts() + 1);
        visitorsService.saveOrUpdate(visitorsHistory);
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        final Visitors visitorsOnline = visitorsService.getById(1);
        visitorsOnline.setCounts(visitorsOnline.getCounts() - 1);
        visitorsService.saveOrUpdate(visitorsOnline);
    }
}
