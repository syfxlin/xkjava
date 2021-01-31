package me.ixk.framework.property;

import java.util.List;
import java.util.Map;

/**
 * 元数据
 *
 * @author Otstar Lin
 * @date 2021/1/31 下午 2:13
 */
public class Metadata extends MapPropertySource<List<String>> {

    public Metadata(final String name, final Map<String, List<String>> source) {
        super(name, source);
    }
}
