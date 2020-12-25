/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.bean;

/**
 * @author Otstar Lin
 * @date 2020/12/25 下午 4:25
 */
public class Early1 {

    private Early2 early2;

    public Early2 getEarly2() {
        return early2;
    }

    public void setEarly2(final Early2 early2) {
        this.early2 = early2;
    }
}
