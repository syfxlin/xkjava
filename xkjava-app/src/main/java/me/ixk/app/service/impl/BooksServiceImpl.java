/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.Books;
import me.ixk.app.mapper.BooksMapper;
import me.ixk.app.service.IBooksService;
import me.ixk.framework.annotations.Service;
import me.ixk.framework.service.ServiceImpl;

/**
 * @author Otstar Lin
 * @date 2020/10/16 上午 10:19
 */
@Service
public class BooksServiceImpl
    extends ServiceImpl<BooksMapper, Books>
    implements IBooksService {}
