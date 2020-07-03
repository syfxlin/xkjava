package me.ixk.framework.http.result;

public class EmptyResult extends HttpResult {

    @Override
    public String render() {
        return "";
    }
}
