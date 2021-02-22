package me.ixk.framework.event;

import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotation.listener.EventListener;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2021/2/20 下午 10:24
 */
@XkJavaTest
@Slf4j
class EventPublisherTest {

    @Test
    void run() {}

    @EventListener(ApplicationBootingEvent.class)
    public void booting() {
        log.info("Listener booting");
    }

    @EventListener(ApplicationBootedEvent.class)
    public void booted() {
        log.info("Listener booted");
    }
}
