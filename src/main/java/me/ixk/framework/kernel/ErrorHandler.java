package me.ixk.framework.kernel;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;

public class ErrorHandler
    extends org.eclipse.jetty.server.handler.ErrorHandler {

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
            "  margin: 0;\n" +
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

        writer.write("<div class=\"flex-center position-ref full-height\">");

        this.writeErrorPageMessage(request, writer, code, message, uri);
        if (showStacks) {
            // TODO: 优化 stacks 的样式
            this.writeErrorPageStacks(request, writer);
        }

        writer.write("</div>");
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
}
