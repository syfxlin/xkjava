/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbProduct;
import me.ixk.app.mapper.TbProductMapper;
import me.ixk.app.service.ITbProductService;
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
public class TbProductServiceImpl
    extends ServiceImpl<TbProductMapper, TbProduct>
    implements ITbProductService {}
