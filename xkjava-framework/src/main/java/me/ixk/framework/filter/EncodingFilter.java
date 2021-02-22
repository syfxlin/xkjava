/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.web.Filter;

/**
 * 编码过滤器
 *
 * @author Otstar Lin
 * @date 2020/10/30 下午 10:45
 */
@Filter(url = "/*", asyncSupported = true)
@Order(Order.HIGHEST_PRECEDENCE)
public class EncodingFilter extends GenericFilter {

    private String requestEncoding;
    private String responseEncoding;

    @Autowired
    public EncodingFilter() {}

    public EncodingFilter(String encoding) {
        this(encoding, encoding);
    }

    public EncodingFilter(String requestEncoding, String responseEncoding) {
        this.requestEncoding = requestEncoding;
        this.responseEncoding = responseEncoding;
    }

    public String getRequestEncoding() {
        return requestEncoding;
    }

    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        if (this.requestEncoding != null) {
            request.setCharacterEncoding(this.requestEncoding);
        }
        if (this.responseEncoding != null) {
            response.setCharacterEncoding(this.responseEncoding);
        }
        chain.doFilter(request, response);
    }
}
