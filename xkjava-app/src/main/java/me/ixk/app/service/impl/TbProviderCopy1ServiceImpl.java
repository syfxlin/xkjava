/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbProviderCopy1;
import me.ixk.app.mapper.TbProviderCopy1Mapper;
import me.ixk.app.service.ITbProviderCopy1Service;
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
public class TbProviderCopy1ServiceImpl
    extends ServiceImpl<TbProviderCopy1Mapper, TbProviderCopy1>
    implements ITbProviderCopy1Service {}
