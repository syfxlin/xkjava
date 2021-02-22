/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbNews;
import me.ixk.app.mapper.TbNewsMapper;
import me.ixk.app.service.ITbNewsService;
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
public class TbNewsServiceImpl
    extends ServiceImpl<TbNewsMapper, TbNews>
    implements ITbNewsService {}
