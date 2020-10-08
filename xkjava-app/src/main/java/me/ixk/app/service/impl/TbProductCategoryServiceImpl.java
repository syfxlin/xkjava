/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.TbProductCategory;
import me.ixk.app.mapper.TbProductCategoryMapper;
import me.ixk.app.service.ITbProductCategoryService;
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
public class TbProductCategoryServiceImpl
    extends ServiceImpl<TbProductCategoryMapper, TbProductCategory>
    implements ITbProductCategoryService {}
