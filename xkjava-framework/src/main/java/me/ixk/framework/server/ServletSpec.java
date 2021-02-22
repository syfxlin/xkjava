package me.ixk.framework.server;

import java.util.Map;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletSecurityElement;

/**
 * Servlet 描述
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 3:21
 */
public class ServletSpec {

    private final String name;
    private final String[] url;
    private final Servlet servlet;
    private final int loadOnStartup;
    private final Map<String, String> initParams;
    private final boolean asyncSupported;
    private final MultipartConfigElement multipartConfig;
    private final ServletSecurityElement servletSecurity;

    public ServletSpec(
        String name,
        String[] url,
        Servlet servlet,
        int loadOnStartup,
        Map<String, String> initParams,
        boolean asyncSupported,
        MultipartConfigElement multipartConfig,
        ServletSecurityElement servletSecurity
    ) {
        this.name = name;
        this.url = url;
        this.servlet = servlet;
        this.loadOnStartup = loadOnStartup;
        this.initParams = initParams;
        this.asyncSupported = asyncSupported;
        this.multipartConfig = multipartConfig;
        this.servletSecurity = servletSecurity;
    }

    public String getName() {
        return name;
    }

    public String[] getUrl() {
        return url;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    public MultipartConfigElement getMultipartConfig() {
        return multipartConfig;
    }

    public ServletSecurityElement getServletSecurity() {
        return servletSecurity;
    }
}
