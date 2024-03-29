/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 标准 JSON 错误响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:29
 */
public class StdErrorJson {

    protected final int status;

    protected final String message;

    protected final String errors;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("@stacks")
    protected Throwable throwable;

    public StdErrorJson(
        final int status,
        final String message,
        final String errors
    ) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public StdErrorJson(
        final int status,
        final String message,
        final String errors,
        final Throwable throwable
    ) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.throwable = throwable;
    }

    public StdErrorJson(final int status, final Throwable throwable) {
        this.status = status;
        this.message = HttpStatus.valueOf(status).getReasonPhrase();
        this.errors = throwable.getMessage();
        this.throwable = throwable;
    }

    public StdErrorJson(final Throwable throwable) {
        this.status = 500;
        this.message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        this.errors = throwable.getMessage();
        this.throwable = throwable;
    }

    public int status() {
        return status;
    }

    public String message() {
        return message;
    }

    public String errors() {
        return errors;
    }

    public List<String> throwable() {
        Throwable th = throwable;
        final List<String> list = new ArrayList<>();
        while (th != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            th.printStackTrace(pw);
            pw.flush();
            list.add(sw.getBuffer().toString());
            th = th.getCause();
        }
        return list;
    }

    public void throwable(final Throwable throwable) {
        this.throwable = throwable;
    }
}
