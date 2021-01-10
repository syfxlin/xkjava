/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.http.HttpMethod;

/**
 * AbstractFrameworkServlet
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:54
 */
public abstract class AbstractFrameworkServlet extends HttpServlet {

    private static final long serialVersionUID = 3052185294763480615L;

    protected abstract void dispatch(
        HttpServletRequest request,
        HttpServletResponse response
    );

    protected final void processRequest(
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        this.dispatch(request, response);
    }

    @Override
    protected void service(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        final String httpMethod = req.getMethod();
        if (
            httpMethod == null || HttpMethod.PATCH.asString().equals(httpMethod)
        ) {
            processRequest(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) {
        this.processRequest(req, resp);
    }

    @Override
    protected void doHead(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) {
        this.processRequest(req, resp);
    }

    @Override
    protected void doPost(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) {
        this.processRequest(req, resp);
    }

    @Override
    protected void doPut(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) {
        this.processRequest(req, resp);
    }

    @Override
    protected void doDelete(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) {
        this.processRequest(req, resp);
    }

    @Override
    protected void doOptions(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void doTrace(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        super.doTrace(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
}
