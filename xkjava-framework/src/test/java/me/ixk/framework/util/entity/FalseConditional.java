/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util.entity;

import me.ixk.framework.annotation.Conditional;
import me.ixk.framework.util.annotation.Parent;
import me.ixk.framework.util.conditional.FalseCondition;

@Conditional({ FalseCondition.class })
@Parent
public class FalseConditional {}
