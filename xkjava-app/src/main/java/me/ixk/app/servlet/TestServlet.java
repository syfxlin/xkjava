/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotation.web.Servlet;

/**
 * @author Otstar Lin
 * @date 2020/10/30 下午 10:24
 */
@Servlet(url = "/test-servlet")
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = -4363999008958068889L;

    @Override
    protected void doGet(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        resp.getWriter().write("TestServlet");
    }
}
