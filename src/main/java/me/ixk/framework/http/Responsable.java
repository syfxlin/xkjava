/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

public interface Responsable {
    Response toResponse(Request request, Response response, Object result);
}
