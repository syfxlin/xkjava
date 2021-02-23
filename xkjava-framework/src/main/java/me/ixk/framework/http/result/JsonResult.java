/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import com.fasterxml.jackson.databind.JsonNode;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.util.Json;

/**
 * JSON 响应
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:10
 */
public class JsonResult extends AbstractHttpResult {

    protected JsonNode jsonNode;

    public JsonResult() {
        this.jsonNode = Json.make().nullNode();
    }

    public JsonResult(final Object object) {
        this.jsonNode = Json.convertToNode(object);
    }

    public JsonResult(final JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public JsonResult json(final JsonNode node) {
        this.jsonNode = node;
        return this;
    }

    public JsonResult json(final Object object) {
        this.jsonNode = Json.convertToNode(object);
        return this;
    }

    public JsonNode jsonNode() {
        return this.jsonNode;
    }

    @Override
    public String render() {
        return this.jsonNode.toString();
    }

    @Override
    public String contentType() {
        return MimeType.APPLICATION_JSON.asString();
    }
}
