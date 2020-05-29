package me.ixk.framework.config;

import java.util.Map;

public interface Config {
    Map<String, Object> config();
    String configName();
}
