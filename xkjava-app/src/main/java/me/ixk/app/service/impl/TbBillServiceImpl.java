/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbBill;
import me.ixk.app.mapper.TbBillMapper;
import me.ixk.app.service.ITbBillService;
import me.ixk.framework.annotation.database.Service;
import me.ixk.framework.service.ServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author syfxlin
 * @since 2020-10-08
 */
@Service
public class TbBillServiceImpl
    extends ServiceImpl<TbBillMapper, TbBill>
    implements ITbBillService {}
