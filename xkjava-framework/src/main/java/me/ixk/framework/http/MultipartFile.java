package me.ixk.framework.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import javax.servlet.http.Part;

/**
 * @author Otstar Lin
 * @date 2021/2/23 下午 6:15
 */
public class MultipartFile {

    private final Part part;

    public MultipartFile(final Part part) {
        this.part = part;
    }

    public String name() {
        return this.part.getName();
    }

    public InputStream stream() throws IOException {
        return this.part.getInputStream();
    }

    public String string() throws IOException {
        return this.string(StandardCharsets.UTF_8);
    }

    public String string(final Charset charset) throws IOException {
        return IoUtil.read(this.part.getInputStream(), charset);
    }

    public String contentType() {
        return this.part.getContentType();
    }

    public String submitName() {
        return this.part.getSubmittedFileName();
    }

    public long size() {
        return this.part.getSize();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public byte[] bytes() throws IOException {
        return IoUtil.readBytes(this.stream());
    }

    public void delete() throws IOException {
        this.part.delete();
    }

    public void transferTo(final File dest) throws IOException {
        this.part.write(dest.getPath());
        if (dest.isAbsolute() && !dest.exists()) {
            Files.copy(
                this.part.getInputStream(),
                dest.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    public void transferTo(final String dest) throws IOException {
        this.transferTo(FileUtil.file(dest));
    }

    public void transferTo(final Path dest) throws IOException {
        Files.copy(
            this.part.getInputStream(),
            dest,
            StandardCopyOption.REPLACE_EXISTING
        );
    }

    public String header(final String name) {
        return this.part.getHeader(name);
    }

    public Collection<String> headers(final String name) {
        return this.part.getHeaders(name);
    }

    public Collection<String> headerNames() {
        return this.part.getHeaderNames();
    }
}
