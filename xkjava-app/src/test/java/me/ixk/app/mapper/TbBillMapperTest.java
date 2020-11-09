/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.mapper;

import me.ixk.framework.database.SqlSessionManager;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

@XkJavaTest
class TbBillMapperTest {

    @Test
    void getAll() {
        XkJava
            .of()
            .make(SqlSessionManager.class)
            .getMapper(TbBillMapper.class)
            .getAll();
    }
}
