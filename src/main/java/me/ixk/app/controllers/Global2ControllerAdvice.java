package me.ixk.app.controllers;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import me.ixk.framework.annotations.ExceptionHandler;
import me.ixk.framework.exceptions.Exception;

//@ControllerAdvice
//@Order(1)
@WebFilter(urlPatterns = "/*")
public class Global2ControllerAdvice implements Filter {

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e) {
        return "error2";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    )
        throws IOException, ServletException {
        System.out.println("filter");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
