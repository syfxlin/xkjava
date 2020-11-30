/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import static me.ixk.framework.utils.DataUtils.caseGet;

import io.github.imsejin.expression.Expression;
import io.github.imsejin.expression.ExpressionParser;
import io.github.imsejin.expression.ParserContext;
import io.github.imsejin.expression.common.TemplateParserContext;
import io.github.imsejin.expression.spel.standard.SpelExpressionParser;
import io.github.imsejin.expression.spel.support.StandardEvaluationContext;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.expression.PropertyPlaceholderHelper.PlaceholderResolver;
import me.ixk.framework.ioc.XkJava;

/**
 * Bean 表达式解析器
 *
 * @author Otstar Lin
 * @date 2020/11/28 下午 10:00
 */
@Component(name = "beanExpressionResolver")
public class BeanExpressionResolver {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>(
        256
    );
    private final ParserContext parserContext = new TemplateParserContext();
    private final PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper(
        "${",
        "}"
    );
    private XkJava app;
    private final PlaceholderResolver envResolver = value -> {
        if (this.app == null) {
            return null;
        }
        return resolveEmbeddedValue(value, this.app.env().getProperties());
    };

    public BeanExpressionResolver(XkJava app) {
        this.app = app;
    }

    public <T> T evaluate(final String expression, final Class<T> returnType) {
        return this.evaluate(
                expression,
                returnType,
                null,
                Collections.emptyMap()
            );
    }

    public static String resolveEmbeddedValue(
        String value,
        Properties properties
    ) {
        final int index = value.indexOf(":");
        String name = value;
        String defaultValue = null;
        if (index != -1) {
            name = value.substring(0, index + 1);
            defaultValue = value.substring(index + 1);
        }
        final Object result = caseGet(name, properties::get);
        if (result == null) {
            return defaultValue;
        }
        return (String) result;
    }

    public <T> T evaluate(
        final String expression,
        final Class<T> returnType,
        final Object root,
        final Map<String, Object> variables
    ) {
        return this.evaluateResolver(
                expression,
                returnType,
                root,
                variables,
                this.envResolver
            );
    }

    protected void customContext(StandardEvaluationContext context) {
        context.setVariable("app", this.app);
        context.setVariable("env", this.app.env());
        context.setVariable("e", this.app.env());
    }

    public <T> T evaluateResolver(
        String expression,
        final Class<T> returnType,
        final Object root,
        final Map<String, Object> variables,
        final PlaceholderResolver resolver
    ) {
        expression =
            placeholderHelper.replacePlaceholders(expression, resolver);
        Expression expr = this.expressionCache.get(expression);
        if (expr == null) {
            expr =
                this.expressionParser.parseExpression(
                        expression,
                        this.parserContext
                    );
            this.expressionCache.put(expression, expr);
        }
        StandardEvaluationContext sec = new StandardEvaluationContext(root);
        sec.addPropertyAccessor(new MapAccessor());
        sec.addPropertyAccessor(new EnvironmentAccessor());
        sec.setBeanResolver(new ContainerBeanResolver(this.app));
        sec.setTypeConverter(new StandardTypeConverter());
        sec.setVariables(variables);
        this.customContext(sec);
        return expr.getValue(sec, returnType);
    }
}
