package me.ixk.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.ibatis.session.TransactionIsolationLevel;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    TransactionIsolationLevel isolation() default TransactionIsolationLevel.READ_COMMITTED;

    Class<? extends Exception>[] rollbackFor() default {  };
}
