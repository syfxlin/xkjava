/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import cn.hutool.http.HtmlUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.WebResolver;
import me.ixk.framework.exceptions.Exception;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.StdErrorJson;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.ByteArrayUtf8Writer;
import me.ixk.framework.utils.Json;
import me.ixk.framework.web.AfterHandlerExceptionResolver;
import me.ixk.framework.web.ExceptionInfo;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;

/**
 * 最终异常处理器
 *
 * @author Otstar Lin
 * @date 2020/11/25 上午 8:14
 */
@WebResolver
@Order(Order.LOWEST_PRECEDENCE)
public class FinallyAfterHandlerExceptionResolver
    implements AfterHandlerExceptionResolver {
    private static final String SHOW_STACK_KEY = "xkjava.app.showErrorStack";
    private final boolean showStack;

    public FinallyAfterHandlerExceptionResolver(Environment env) {
        this.showStack = env.getBoolean(SHOW_STACK_KEY, Boolean.FALSE);
    }

    @Override
    public Response resolveException(
        final Throwable e,
        final ExceptionInfo info,
        final WebContext context,
        final WebDataBinder binder
    ) {
        try {
            final Request request = info.getRequest();
            final Response response = info.getResponse();
            if (request.ajax()) {
                this.handleJson(e, request, response);
            } else {
                this.handleHtml(e, request, response);
            }
            return response;
        } catch (IOException ex) {
            throw new Exception(ex);
        }
    }

    private Status getStatus(final Throwable e, final Response response) {
        if (e instanceof HttpException) {
            return new Status(
                ((HttpException) e).getCode(),
                ((HttpException) e).getReason()
            );
        }
        return new Status(response.getStatus(), e.getMessage());
    }

    private void handleHtml(
        final Throwable e,
        final Request request,
        final Response response
    )
        throws IOException {
        response.contentType(MimeType.TEXT_HTML);
        final Status status = this.getStatus(e, response);
        final ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer(4096);
        this.handleErrorPage(writer, e, status);
        writer.flush();
        response.contentLength(writer.size());
        response.status(status.getCode());
        writer.writeTo(response.getOutputStream());
        writer.destroy();
    }

    private void handleJson(
        final Throwable e,
        final Request request,
        final Response response
    )
        throws IOException {
        response.contentType(MimeType.APPLICATION_JSON);
        final Status status = this.getStatus(e, response);
        final ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer(4096);
        this.handleErrorJsonContent(writer, e, status);
        writer.flush();
        response.contentLength(writer.size());
        response.status(status.getCode());
        writer.writeTo(response.getOutputStream());
        writer.destroy();
    }

    private void handleErrorJsonContent(
        ByteArrayUtf8Writer writer,
        Throwable e,
        Status status
    )
        throws IOException {
        StdErrorJson errorJson = new StdErrorJson(
            status.getCode(),
            status.getMessage(),
            e != null ? e.getMessage() : status.getMessage()
        );
        if (this.showStack) {
            errorJson.setThrowable(e);
        }
        String json = Json.stringify(errorJson);
        writer.write(
            json != null
                ? json
                : "{\n" +
                "    \"status\": \"500\",\n" +
                "    \"message\": \"Json stringify error\",\n" +
                "    \"errors\": \"ErrorHandler json stringify failed\"\n" +
                "}"
        );
    }

    private void handleErrorPage(
        final ByteArrayUtf8Writer writer,
        final Throwable e,
        final Status status
    )
        throws IOException {
        writer.write("<html>\n<head>\n");
        this.writeErrorPageHead(writer, status);
        writer.write("</head>\n<body>");
        this.writeErrorPageBody(writer, status);
        if (this.showStack) {
            this.writeErrorPageStacks(writer, e);
        }
        writer.write("\n</body>\n</html>\n");
    }

    private void writeErrorPageHead(ByteArrayUtf8Writer writer, Status status)
        throws IOException {
        writer.write(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n"
        );
        writer.write(
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        );
        writer.write("<title>Error ");
        writer.write(Integer.toString(status.getCode()));

        writer.write(" - ");
        writer.write(HtmlUtil.escape(status.getMessage()));
        writer.write("</title>\n");

        this.writeErrorPageFont(writer);
        this.writeErrorPageStyle(writer);
    }

    private void writeErrorPageFont(ByteArrayUtf8Writer writer)
        throws IOException {
        writer.write(
            "<link rel=\"dns-prefetch\" href=\"//fonts.gstatic.com\">\n"
        );
        writer.write(
            "<link href=\"https://fonts.googleapis.com/css?family=Nunito\" rel=\"stylesheet\">\n"
        );
    }

    private void writeErrorPageStyle(ByteArrayUtf8Writer writer)
        throws IOException {
        writer.write("<style>");
        writer.write(
            "html,\n" +
            "body {\n" +
            "  background-color: #fff;\n" +
            "  color: #636b6f;\n" +
            "  font-family: 'Nunito', sans-serif;\n" +
            "  font-weight: 100;\n" +
            "  height: 100vh;\n" +
            "  width: 100vw;" +
            "  margin: 0;\n" +
            "  display: flex;" +
            "}\n" +
            ".full-height {\n" +
            "  height: 100vh;\n" +
            "}\n" +
            ".flex-center {\n" +
            "  align-items: center;\n" +
            "  display: flex;\n" +
            "  justify-content: center;\n" +
            "}\n" +
            ".position-ref {\n" +
            "  position: relative;\n" +
            "}\n" +
            ".code {\n" +
            "  border-right: 2px solid;\n" +
            "  font-size: 26px;\n" +
            "  padding: 0 15px 0 15px;\n" +
            "  text-align: center;\n" +
            "}\n" +
            ".message {\n" +
            "  font-size: 18px;\n" +
            "  text-align: center;\n" +
            "}\n" +
            ".flex-1 {" +
            "  flex: 1;" +
            "}\n" +
            ".stack {" +
            "  width: 70vw;" +
            "  overflow-x: hidden;\n" +
            "  overflow-y: auto;\n" +
            "  white-space: pre-wrap;\n" +
            "  word-break: break-all;\n" +
            "}\n" +
            ".stack pre {" +
            "  white-space: pre-wrap;\n" +
            "  word-break: break-all;\n" +
            "}\n"
        );
        writer.write("</style>\n");
    }

    private void writeErrorPageBody(ByteArrayUtf8Writer writer, Status status)
        throws IOException {
        writer.write(
            "<div class=\"flex-center position-ref full-height flex-1\">"
        );
        writer.write("<div class=\"code\">" + status.getCode() + "</div>");
        writer.write("<div class=\"message\" style=\"padding: 10px;\">");
        writer.write(status.getMessage());
        writer.write("</div>");
        writer.write("</div>");
    }

    private void writeErrorPageStacks(ByteArrayUtf8Writer writer, Throwable e)
        throws IOException {
        if (e != null) {
            writer.write("<h3>Caused by:</h3><pre>");
            try (
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)
            ) {
                e.printStackTrace(pw);
                pw.flush();
                writer.write(HtmlUtil.escape(sw.getBuffer().toString()));
            }
            writer.write("</pre>\n");
        }
    }

    private static class Status {
        private final int code;
        private final String message;

        public Status(int code, String message) {
            this.code = code;
            if (message == null) {
                final HttpStatus resolve = HttpStatus.resolve(this.code);
                this.message =
                    Objects
                        .requireNonNullElse(
                            resolve,
                            HttpStatus.INTERNAL_SERVER_ERROR
                        )
                        .getReasonPhrase();
            } else {
                this.message = message;
            }
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
