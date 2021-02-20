/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbOrderDetail;
import me.ixk.app.mapper.TbOrderDetailMapper;
import me.ixk.app.service.ITbOrderDetailService;
import me.ixk.framework.annotation.Service;
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
public class TbOrderDetailServiceImpl
    extends ServiceImpl<TbOrderDetailMapper, TbOrderDetail>
    implements ITbOrderDetailService {}
