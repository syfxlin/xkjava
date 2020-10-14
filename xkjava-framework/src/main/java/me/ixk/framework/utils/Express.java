/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import io.github.imsejin.expression.Expression;
import io.github.imsejin.expression.ExpressionParser;
import io.github.imsejin.expression.spel.standard.SpelExpressionParser;
import io.github.imsejin.expression.spel.support.StandardEvaluationContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;

/**
 * 表达式解析工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:02
 */
public class Express {
    protected static final ExpressionParser PARSER = new SpelExpressionParser();

    public static <T> T evaluateApp(
        final String expression,
        final Class<T> returnType
    ) {
        return evaluateApp(expression, returnType, null);
    }

    public static <T> T evaluateApp(
        final String expression,
        final Class<T> returnType,
        final Object root
    ) {
        final Map<String, Object> variables = new ConcurrentHashMap<>(10);
        final XkJava app = XkJava.of();
        final Environment env = app.make(Environment.class);
        variables.put("app", app);
        variables.put("env", env);
        variables.put("e", env.getProperties());
        return evaluate(expression, returnType, root, variables);
    }

    public static <T> T evaluate(
        final String expression,
        final Class<T> returnType,
        final Object root,
        final Map<String, Object> variables
    ) {
        final Expression expr = PARSER.parseExpression(expression);
        final StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(root);
        context.setVariables(variables);
        return expr.getValue(context, returnType);
    }
}
