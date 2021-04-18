package me.ixk.framework.resource;

import java.io.File;
import org.jetbrains.annotations.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 3:15
 */
public abstract class AbstractResource implements Resource {

    private final String name;

    public AbstractResource(@Nullable final String name) {
        this.name = name;
    }

    @Override
    public @Nullable String getName() {
        if (this.name != null) {
            return this.name;
        }
        final File file = this.getFile();
        if (file != null) {
            return file.getName();
        }
        return null;
    }
}
