/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils.entity;

import me.ixk.framework.annotations.Conditional;
import me.ixk.framework.utils.annotation.Parent;
import me.ixk.framework.utils.conditional.FalseCondition;

@Conditional({ FalseCondition.class })
@Parent
public class FalseConditional {}
