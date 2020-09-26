/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

public class AspectPointcut {
  protected PointcutParser pointcutParser;

  protected PointcutExpression pointcutExpression;

  private static final Set<PointcutPrimitive> POINTCUT_PRIMITIVES = new HashSet<>();

  static {
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.ARGS);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.THIS);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.TARGET);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.WITHIN);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
    POINTCUT_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
  }

  public AspectPointcut(String expression) {
    this(expression, POINTCUT_PRIMITIVES);
  }

  public AspectPointcut(
    String expression,
    Set<PointcutPrimitive> pointcutPrimitives
  ) {
    this.pointcutParser =
      PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
        pointcutPrimitives
      );
    this.pointcutExpression =
      this.pointcutParser.parsePointcutExpression(expression);
  }

  public boolean matches(Class<?> target) {
    // 排除注解
    // if (
    //     this.pointcutExpression.getPointcutExpression()
    //         .indexOf("@annotation") ==
    //     0
    // ) {
    //     return false;
    // }
    return this.pointcutExpression.couldMatchJoinPointsInType(target);
  }

  public boolean matches(Method method) {
    ShadowMatch match = this.pointcutExpression.matchesMethodExecution(method);
    if (match.alwaysMatches()) {
      return true;
    } else if (match.neverMatches()) {
      return false;
    }
    return false;
  }
}