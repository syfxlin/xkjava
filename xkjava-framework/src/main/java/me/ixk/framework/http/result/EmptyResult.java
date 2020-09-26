/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

public class EmptyResult extends HttpResult {

    @Override
    public String render() {
        return "";
    }
}
