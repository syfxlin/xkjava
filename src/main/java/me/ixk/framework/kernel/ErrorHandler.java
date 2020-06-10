package me.ixk.framework.kernel;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.ByteArrayUtf8Writer;
import me.ixk.framework.http.ResponseReason;
import me.ixk.framework.http.StdErrorJson;
import me.ixk.framework.utils.JSON;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class ErrorHandler
    extends org.eclipse.jetty.server.handler.ErrorHandler {
    private static final Logger LOG = Log.getLogger(ErrorHandler.class);

    @Override
    public void handle(
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response
    )
        throws IOException {
        String method = request.getMethod();
        if (
            !HttpMethod.GET.is(method) &&
            !HttpMethod.POST.is(method) &&
            !HttpMethod.HEAD.is(method)
        ) {
            baseRequest.setHandled(true);
            return;
        }
        if (!this.isJson(request)) {
            this.handleHtml(target, baseRequest, request, response);
        } else {
            this.handleJson(target, baseRequest, request, response);
        }
    }

    protected boolean isJson(HttpServletRequest request) {
        String xrw = request.getHeader("X-Requested-With");
        String acc = request.getHeader("Accept");
        return (
            "XMLHttpRequest".equals(xrw) ||
            (acc != null && acc.startsWith("application/json"))
        );
    }

    protected void handleHtml(
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response
    )
        throws IOException {
        if (this instanceof ErrorPageMapper) {
            String error_page = ((ErrorPageMapper) this).getErrorPage(request);
            if (error_page != null && request.getServletContext() != null) {
                String old_error_page = (String) request.getAttribute(
                    ERROR_PAGE
                );
                if (
                    old_error_page == null || !old_error_page.equals(error_page)
                ) {
                    request.setAttribute(ERROR_PAGE, error_page);

                    Dispatcher dispatcher = (Dispatcher) request
                        .getServletContext()
                        .getRequestDispatcher(error_page);
                    try {
                        if (dispatcher != null) {
                            dispatcher.error(request, response);
                            return;
                        }
                        LOG.warn("No error page " + error_page);
                    } catch (ServletException e) {
                        LOG.warn(Log.EXCEPTION, e);
                        return;
                    }
                }
            }
        }

        baseRequest.setHandled(true);
        response.setContentType(MimeTypes.Type.TEXT_HTML.asString());
        if (this.getCacheControl() != null) response.setHeader(
            HttpHeader.CACHE_CONTROL.asString(),
            this.getCacheControl()
        );
        ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer(4096);
        Result result = new Result(request, response);
        this.handleErrorPage(
                request,
                writer,
                result.getCode(),
                result.getReason()
            );
        writer.flush();
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        writer.destroy();
    }

    protected void handleJson(
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response
    )
        throws IOException {
        baseRequest.setHandled(true);
        response.setContentType(MimeTypes.Type.APPLICATION_JSON.asString());
        if (this.getCacheControl() != null) response.setHeader(
            HttpHeader.CACHE_CONTROL.asString(),
            this.getCacheControl()
        );
        ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer(4096);
        Result result = new Result(request, response);
        this.handleErrorJsonContent(
                request,
                writer,
                result.getCode(),
                result.getReason()
            );
        writer.flush();
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        writer.destroy();
    }

    protected void handleErrorJsonContent(
        HttpServletRequest request,
        Writer writer,
        int code,
        String message
    )
        throws IOException {
        Throwable th = (Throwable) request.getAttribute(
            "javax.servlet.error.exception"
        );
        StdErrorJson errorJson = new StdErrorJson(
            code,
            message,
            th != null ? th.getMessage() : message
        );
        String json = JSON.stringify(errorJson);
        writer.write(
            json != null
                ? json
                : "{\n" +
                "    \"status\": \"500\",\n" +
                "    \"message\": \"JSON stringify error\",\n" +
                "    \"errors\": \"ErrorHandler json stringify failed\"\n" +
                "}"
        );
    }

    @Override
    protected void writeErrorPageHead(
        HttpServletRequest request,
        Writer writer,
        int code,
        String message
    )
        throws IOException {
        writer.write(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n"
        );
        writer.write(
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        );
        writer.write("<title>Error ");
        writer.write(Integer.toString(code));

        if (this.getShowMessageInTitle()) {
            writer.write(" - ");
            write(writer, message);
        }
        writer.write("</title>\n");

        this.writeErrorPageFont(request, writer, code, message);
        this.writeErrorPageStyle(request, writer, code, message);
    }

    protected void writeErrorPageFont(
        HttpServletRequest request,
        Writer writer,
        int code,
        String message
    )
        throws IOException {
        writer.write(
            "<link rel=\"dns-prefetch\" href=\"//fonts.gstatic.com\">\n"
        );
        writer.write(
            "<link href=\"https://fonts.googleapis.com/css?family=Nunito\" rel=\"stylesheet\">\n"
        );
    }

    protected void writeErrorPageStyle(
        HttpServletRequest request,
        Writer writer,
        int code,
        String message
    )
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

    @Override
    protected void writeErrorPageBody(
        HttpServletRequest request,
        Writer writer,
        int code,
        String message,
        boolean showStacks
    )
        throws IOException {
        String uri = request.getRequestURI();

        writer.write(
            "<div class=\"flex-center position-ref full-height flex-1\">"
        );
        this.writeErrorPageMessage(request, writer, code, message, uri);
        writer.write("</div>");

        if (showStacks) {
            writer.write("<div class=\"stack\">");
            this.writeErrorPageStacks(request, writer);
            writer.write("</div>");
        }
    }

    @Override
    protected void writeErrorPageMessage(
        HttpServletRequest request,
        Writer writer,
        int code,
        String message,
        String uri
    )
        throws IOException {
        writer.write("<div class=\"code\">" + code + "</div>");
        writer.write("<div class=\"message\" style=\"padding: 10px;\">");
        writer.write(message);
        writer.write("</div>");
    }

    public static class Result {
        private int code;
        private String reason;

        public Result(
            HttpServletRequest request,
            HttpServletResponse response
        ) {
            Throwable th = (Throwable) request.getAttribute(
                "javax.servlet.error.exception"
            );
            if (th instanceof HttpException) {
                HttpException exception = (HttpException) th;
                this.code = exception.getStatus();
                this.reason = exception.getMessage();
            } else {
                this.code = response.getStatus();
                this.reason = ResponseReason.getMessage(code);
            }
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
