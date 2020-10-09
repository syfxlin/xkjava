/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;
import java.util.Map;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.Environment;

public abstract class Express {
    protected static final ExpressRunner runner = new ExpressRunner();

    static {
        runner.addFunction("$", new ValueOperator());
    }

    public static Object executeEnv(String express) {
        XkJava app = XkJava.of();
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("env", app.make(Environment.class));
        return execute(express, (IExpressContext<String, Object>) context);
    }

    public static Object execute(String express, Map<String, Object> map) {
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.putAll(map);
        return execute(express, (IExpressContext<String, Object>) context);
    }

    public static Object execute(
        String express,
        IExpressContext<String, Object> context
    ) {
        try {
            return runner.execute(express, context, null, true, false);
        } catch (Exception e) {
            return null;
        }
    }

    public static class ValueOperator extends OperatorBase {

        @Override
        public OperateData executeInner(
            InstructionSetContext context,
            ArraySwap list
        )
            throws Exception {
            Object[] parameters = new Object[list.length];
            Environment env = (Environment) context.get("env");
            for (int i = 0; i < list.length; i++) {
                if (
                    list.get(i) == null &&
                    QLExpressRunStrategy.isAvoidNullPointer()
                ) {
                    parameters[i] = null;
                } else {
                    String[] keys = list
                        .get(i)
                        .getObject(context)
                        .toString()
                        .split(":");
                    parameters[i] = env.get(keys[0]);
                    if (parameters[i] == null && keys.length == 2) {
                        parameters[i] = keys[1];
                    }
                }
            }
            Object result = parameters;
            if (parameters.length == 1) {
                result = parameters[0];
            }
            return OperateDataCacheManager.fetchOperateData(
                result,
                ExpressUtil.getSimpleDataType(result.getClass())
            );
        }
    }
}
