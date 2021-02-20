/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbProvider;
import me.ixk.app.mapper.TbProviderMapper;
import me.ixk.app.service.ITbProviderService;
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
public class TbProviderServiceImpl
    extends ServiceImpl<TbProviderMapper, TbProvider>
    implements ITbProviderService {}
