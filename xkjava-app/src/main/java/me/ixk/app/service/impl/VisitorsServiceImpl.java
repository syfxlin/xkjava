/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.Visitors;
import me.ixk.app.mapper.VisitorsMapper;
import me.ixk.app.service.IVisitorsService;
import me.ixk.framework.annotation.Service;
import me.ixk.framework.service.ServiceImpl;

@Service
public class VisitorsServiceImpl
    extends ServiceImpl<VisitorsMapper, Visitors>
    implements IVisitorsService {}
