/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import me.ixk.app.auth.Auth;
import me.ixk.app.entity.Visitors;
import me.ixk.app.service.impl.VisitorsServiceImpl;
import me.ixk.framework.ioc.XkJava;

@WebListener
public class LoginCountListener implements HttpSessionAttributeListener {
    final VisitorsServiceImpl visitorsService = XkJava
        .of()
        .make(VisitorsServiceImpl.class);

    @Override
    public void attributeAdded(final HttpSessionBindingEvent event) {
        if (event.getName().equals(Auth.getName())) {
            final Visitors visitors = visitorsService.getById(3);
            final List<String> users = new ArrayList<>(
                Arrays.asList(visitors.getUsers().split(","))
            );
            users.add(event.getValue().toString());
            visitors.setUsers(String.join(",", users));
            visitorsService.saveOrUpdate(visitors);
        }
    }

    @Override
    public void attributeRemoved(final HttpSessionBindingEvent event) {
        if (event.getName().equals(Auth.getName())) {
            final Visitors visitors = visitorsService.getById(3);
            final List<String> users = new ArrayList<>(
                Arrays.asList(visitors.getUsers().split(","))
            );
            users.remove((String) event.getValue());
            visitors.setUsers(String.join(",", users));
            visitorsService.saveOrUpdate(visitors);
        }
    }

    @Override
    public void attributeReplaced(final HttpSessionBindingEvent event) {
        //
    }
}
