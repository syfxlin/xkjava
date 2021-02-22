/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbProviderCopy;
import me.ixk.app.mapper.TbProviderCopyMapper;
import me.ixk.app.service.ITbProviderCopyService;
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
public class TbProviderCopyServiceImpl
    extends ServiceImpl<TbProviderCopyMapper, TbProviderCopy>
    implements ITbProviderCopyService {}
