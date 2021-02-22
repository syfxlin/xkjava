/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util.entity;

import me.ixk.framework.annotation.core.Conditional;
import me.ixk.framework.util.annotation.Parent;
import me.ixk.framework.util.conditional.TrueCondition;

@Conditional({ TrueCondition.class })
@Parent
public class TrueConditional {}
