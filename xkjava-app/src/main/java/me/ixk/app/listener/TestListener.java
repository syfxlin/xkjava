/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotation.web.Listener;

/**
 * @author Otstar Lin
 * @date 2020/10/30 下午 9:50
 */
@Listener
@Slf4j
public class TestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(final ServletRequestEvent sre) {
        log.info("TestListener destroy");
    }

    @Override
    public void requestInitialized(final ServletRequestEvent sre) {
        log.info("TestListener initialized");
    }
}
