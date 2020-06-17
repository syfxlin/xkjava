package me.ixk.framework.newioc;

import me.ixk.app.beans.User;
import me.ixk.framework.annotations.ScopeType;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContainerTest {
    Container container = new Container();

    @BeforeEach
    void setUp() {
        ApplicationContext applicationContext = new ApplicationContext();
        RequestContext requestContext = new RequestContext();
        requestContext.createContext();
        container.registerContext(ContextName.APPLICATION, applicationContext);
        container.registerContext(ContextName.REQUEST, requestContext);
    }

    @Test
    void testInstanceAndMake() {
        container.bind(
            User.class,
            (container, args) -> new User("admin", 20),
            "user",
            ScopeType.SINGLETON,
            false
        );

        container.instance(
            "user1",
            new User("user1", 20),
            "user01",
            ScopeType.SINGLETON
        );
        User user1 = container.doMake("user1", User.class);
        Assertions.assertEquals("user1", user1.getName());

        container.instance(
            "user2",
            new User("user2", 20),
            "user02",
            ScopeType.REQUEST
        );
        User user2 = container.doMake("user2", User.class);
        Assertions.assertEquals("user2", user2.getName());
        Assertions.assertTrue(
            container.getContextByName(ContextName.REQUEST).hasBinding("user2")
        );

        container.doRemove("user2");
        Assertions.assertFalse(
            container.getContextByName(ContextName.REQUEST).hasBinding("user2")
        );
        Assertions.assertFalse(
            container.getContextByName(ContextName.REQUEST).hasBinding("user02")
        );
    }
}
