/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.bean;

/**
 * @author Otstar Lin
 * @date 2020/12/25 下午 4:25
 */
public class Early2 {

    private Early1 early1;

    public Early1 getEarly1() {
        return early1;
    }

    public void setEarly1(final Early1 early1) {
        this.early1 = early1;
    }
}
