package me.ixk.framework.http.result;

import org.eclipse.jetty.http.MimeTypes;

public class TextResult extends HttpResult {
    protected String text;

    public TextResult(String text) {
        this.text = text;
    }

    public void with(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String render() {
        return this.text;
    }

    @Override
    public String contentType() {
        return MimeTypes.Type.TEXT_PLAIN.asString();
    }
}
