/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils.entity;

import me.ixk.framework.annotations.Conditional;
import me.ixk.framework.utils.annotation.Parent;
import me.ixk.framework.utils.conditional.TrueCondition;

@Conditional({ TrueCondition.class })
@Parent
public class TrueConditional {}
