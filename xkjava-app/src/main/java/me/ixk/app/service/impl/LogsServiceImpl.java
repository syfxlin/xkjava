/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.service.impl;

import me.ixk.app.entity.Logs;
import me.ixk.app.mapper.LogsMapper;
import me.ixk.app.service.ILogsService;
import me.ixk.framework.annotation.Service;
import me.ixk.framework.service.ServiceImpl;

@Service
public class LogsServiceImpl
    extends ServiceImpl<LogsMapper, Logs>
    implements ILogsService {}
