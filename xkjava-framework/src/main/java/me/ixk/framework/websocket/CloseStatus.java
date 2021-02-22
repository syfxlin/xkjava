//
//  ========================================================================
//  Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package me.ixk.framework.websocket;

import java.util.Objects;

/**
 * 关闭状态
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:19
 */
public class CloseStatus {

    public static final CloseStatus NORMAL = new CloseStatus(1000);

    public static final CloseStatus GOING_AWAY = new CloseStatus(1001);

    public static final CloseStatus PROTOCOL_ERROR = new CloseStatus(1002);

    public static final CloseStatus NOT_ACCEPTABLE = new CloseStatus(1003);

    public static final CloseStatus NO_STATUS_CODE = new CloseStatus(1005);

    public static final CloseStatus NO_CLOSE_FRAME = new CloseStatus(1006);

    public static final CloseStatus BAD_DATA = new CloseStatus(1007);

    public static final CloseStatus POLICY_VIOLATION = new CloseStatus(1008);

    public static final CloseStatus TOO_BIG_TO_PROCESS = new CloseStatus(1009);

    public static final CloseStatus REQUIRED_EXTENSION = new CloseStatus(1010);

    public static final CloseStatus SERVER_ERROR = new CloseStatus(1011);

    public static final CloseStatus SERVICE_RESTARTED = new CloseStatus(1012);

    public static final CloseStatus SERVICE_OVERLOAD = new CloseStatus(1013);

    public static final CloseStatus TLS_HANDSHAKE_FAILURE = new CloseStatus(
        1015
    );

    public static final CloseStatus SESSION_NOT_RELIABLE = new CloseStatus(
        4500
    );

    private final int code;
    private final String reason;

    public CloseStatus(final int code) {
        this(code, null);
    }

    public CloseStatus(final int code, final String reason) {
        if (code < 1000 || code > 5000) {
            throw new IllegalArgumentException("Invalid status code");
        }
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CloseStatus that = (CloseStatus) o;
        return code == that.code && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, reason);
    }

    @Override
    public String toString() {
        return (
            "CloseStatus{" + "code=" + code + ", reason='" + reason + '\'' + '}'
        );
    }
}
