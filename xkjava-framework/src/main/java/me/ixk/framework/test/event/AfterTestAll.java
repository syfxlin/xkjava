package me.ixk.framework.test.event;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.listener.EventListener;

/**
 * @author Otstar Lin
 * @date 2021/2/21 下午 4:12
 */
@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@Documented
@EventListener(AfterTestAllEvent.class)
public @interface AfterTestAll {
}
