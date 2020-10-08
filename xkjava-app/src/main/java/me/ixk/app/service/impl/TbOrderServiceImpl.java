/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbOrder;
import me.ixk.app.mapper.TbOrderMapper;
import me.ixk.app.service.ITbOrderService;
import me.ixk.framework.annotations.Service;
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
public class TbOrderServiceImpl
    extends ServiceImpl<TbOrderMapper, TbOrder>
    implements ITbOrderService {}
