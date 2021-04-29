/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.ReflectUtil;
import java.util.Map;
import me.ixk.framework.ioc.bean.AutoWiredTest;
import me.ixk.framework.ioc.bean.DataBinderUser;
import me.ixk.framework.ioc.bean.Early1;
import me.ixk.framework.ioc.bean.Early2;
import me.ixk.framework.ioc.bean.Early3;
import me.ixk.framework.ioc.bean.TypeUser;
import me.ixk.framework.ioc.bean.User;
import me.ixk.framework.ioc.binder.DefaultDataBinder;
import me.ixk.framework.ioc.context.PrototypeContext;
import me.ixk.framework.ioc.context.ScopeType;
import me.ixk.framework.ioc.context.SingletonContext;
import me.ixk.framework.ioc.factory.FactoryBean;
import me.ixk.framework.ioc.injector.DefaultMethodInjector;
import me.ixk.framework.ioc.injector.DefaultParameterInjector;
import me.ixk.framework.ioc.injector.DefaultPropertyInjector;
import me.ixk.framework.ioc.injector.PropertiesValueInjector;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/5 下午 4:17
 */
class ContainerTest {

    static final Container container = new Container();

    static {
        container.registerContext(ScopeType.SINGLETON, new SingletonContext());
        container.registerContext(ScopeType.PROTOTYPE, new PrototypeContext());
        container.addParameterInjector(new DefaultParameterInjector());
        container.addInstanceInjector(new PropertiesValueInjector());
        container.addInstanceInjector(new DefaultPropertyInjector());
        container.addInstanceInjector(new DefaultMethodInjector());
        container.instance(new User("syfxlin", 20));
    }

    @Test
    void alias() {
        container.setAlias("User", "user");
        assertNotNull(container.make("User", User.class));

        assertTrue(container.hasAlias("User"));

        assertEquals("user", container.getAlias("User"));

        container.removeAlias("User");

        assertNull(container.getAlias("User"));
    }

    @Test
    void attribute() {
        container.setAttribute("attr", "value", ScopeType.SINGLETON);
        assertEquals("value", container.getAttribute("attr"));

        assertTrue(container.hasAttribute("attr"));

        container.removeAttribute("attr");

        assertFalse(container.hasAttribute("attr"));
    }

    @Test
    void bind() {
        // name, type
        container.bind("test1", AutoWiredTest.class);
        final AutoWiredTest test1 = container.make(
            "test1",
            AutoWiredTest.class
        );
        assertEquals("syfxlin", test1.getUser().getName());

        // name, factory
        container.bind(
            "test2",
            new FactoryBean<String>() {
                @Override
                public String getObject() {
                    return "value";
                }

                @Override
                public Class<?> getObjectType() {
                    return String.class;
                }
            }
        );
        final String test2 = container.make("test2", String.class);
        assertEquals("value", test2);

        // type
        container.bind(TypeUser.class);
        final TypeUser typeUser = container.make(TypeUser.class);
        assertEquals("syfxlin", typeUser.getUser().getName());

        // factory
        container.bind(
            new FactoryBean<Integer>() {
                @Override
                public Integer getObject() {
                    return 1;
                }

                @Override
                public Class<?> getObjectType() {
                    return Integer.class;
                }
            }
        );
        final Integer factoryInteger = container.make(Integer.class);
        assertEquals(1, factoryInteger);
    }

    @Test
    void instance() {
        container.instance("user1", new User("syfxlin", 20));
        assertEquals("syfxlin", container.make("user1", User.class).getName());

        assertEquals("syfxlin", container.make("User", User.class).getName());
    }

    @Test
    void make() {
        assertEquals("syfxlin", container.make(User.class).getName());
        assertEquals("syfxlin", container.make("User", User.class).getName());

        assertEquals(
            "syfxlin",
            container
                .make(
                    DataBinderUser.class,
                    new DefaultDataBinder(Map.of("name", "syfxlin"))
                )
                .getName()
        );
    }

    @Test
    void remove() {
        container.bind("remove1", AutoWiredTest.class);
        assertTrue(container.has("remove1"));
        container.remove("remove1");
        assertFalse(container.has("remove1"));
        assertTrue(
            container.getBindingNamesByType().get(AutoWiredTest.class).isEmpty()
        );
    }

    @Test
    void call() throws NoSuchMethodException {
        final User result1 = container.call(
            this,
            this.getClass().getMethod("method1", User.class),
            User.class
        );
        assertEquals("syfxlin", result1.getName());

        final String result2 = container.call("user", "getName", String.class);
        assertEquals("syfxlin", result2);

        final String result3 = container.call(
            User.class,
            "getName",
            String.class
        );
        assertEquals("syfxlin", result3);

        assertEquals(
            "syfxlin",
            container.call(User.class.getMethod("getName"))
        );

        assertEquals(
            "syfxlin",
            container.call(this, "method1", User.class).getName()
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void early() {
        container.bind(Early1.class);
        container.bind(Early2.class);
        container.bind(Early3.class);

        final Early1 early1 = container.make(Early1.class);
        final Early2 early2 = container.make(Early2.class);
        final Early3 early3 = container.make(Early3.class);
        assertEquals(early1, early3.getEarly1());
        assertEquals(early2, early1.getEarly2());
        assertEquals(early3, early2.getEarly3());

        assertNull(
            (
                (ThreadLocal<Map<String, Object>>) ReflectUtil.getFieldValue(
                    container,
                    "earlyBeans"
                )
            ).get()
        );
    }

    public User method1(final User user) {
        return user;
    }
}
