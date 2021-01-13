/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.exceptions.ResponseException;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2020/12/20 下午 7:42
 */
public class FileResult extends AbstractHttpResult {

    protected static final Logger log = LoggerFactory.getLogger(
        FileResult.class
    );

    private static final int DEFAULT_BUFFER_SIZE = 20480;
    private static final long DEFAULT_EXPIRE_TIME = 604800000L;
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    private File file;
    private String contentType;
    private String fileName;
    private boolean async;

    public FileResult(String path) {
        this(path, true);
    }

    public FileResult(File file) {
        this(file, true);
    }

    public FileResult(String path, boolean async) {
        this.with(path);
        this.async = async;
    }

    public FileResult(File file, boolean async) {
        this.with(file);
        this.async = async;
    }

    public FileResult with(String path) {
        try {
            this.file = ResourceUtils.getFile(path);
            if (this.contentType == null) {
                this.contentType = Files.probeContentType(this.file.toPath());
            }
            if (this.fileName == null) {
                this.fileName = this.file.getName();
            }
        } catch (IOException e) {
            throw new ResponseException(e);
        }
        return this;
    }

    public FileResult with(File file) {
        try {
            this.file = file;
            if (this.contentType == null) {
                this.contentType = Files.probeContentType(this.file.toPath());
            }
            if (this.fileName == null) {
                this.fileName = this.file.getName();
            }
        } catch (IOException e) {
            throw new ResponseException(e);
        }
        return this;
    }

    public FileResult contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public FileResult contentType(MimeType contentType) {
        return this.contentType(contentType.asString());
    }

    public FileResult name(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public boolean async() {
        return async;
    }

    public void async(final boolean async) {
        this.async = async;
    }

    @Override
    public boolean toResponse(
        Request request,
        Response response,
        Object result
    ) throws IOException {
        long length = this.file.length();
        FileTime lastModifiedObj = FileTime.fromMillis(
            this.file.lastModified()
        );

        if (StrUtil.isEmpty(fileName)) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return true;
        }
        long lastModified = LocalDateTime
            .ofInstant(
                lastModifiedObj.toInstant(),
                ZoneId.of(ZoneOffset.systemDefault().getId())
            )
            .toEpochSecond(ZoneOffset.UTC);

        // Validate request headers for caching ---------------------------------------------------

        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && HttpUtils.matches(ifNoneMatch, fileName)) {
            response.setHeader(
                "ETag",
                URLEncoder.encode(
                    new String(
                        fileName.getBytes(),
                        StandardCharsets.ISO_8859_1
                    ),
                    StandardCharsets.ISO_8859_1
                )
            ); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (
            ifNoneMatch == null &&
            ifModifiedSince != -1 &&
            ifModifiedSince + 1000 > lastModified
        ) {
            response.setHeader(
                "ETag",
                URLEncoder.encode(
                    new String(
                        fileName.getBytes(),
                        StandardCharsets.ISO_8859_1
                    ),
                    StandardCharsets.ISO_8859_1
                )
            ); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }

        // Validate request headers for resume ----------------------------------------------------

        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !HttpUtils.matches(ifMatch, fileName)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return true;
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (
            ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified
        ) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return true;
        }

        // Validate and process range -------------------------------------------------------------

        // Prepare some variables. The full Range represents the complete file.
        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<>();

        // Validate and process Range and If-Range headers.
        String range = request.getHeader("Range");
        if (range != null) {
            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(
                    HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE
                );
                return true;
            }

            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(fileName)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                    long start = Range.sublong(part, 0, part.indexOf("-"));
                    long end = Range.sublong(
                        part,
                        part.indexOf("-") + 1,
                        part.length()
                    );

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        response.setHeader(
                            "Content-Range",
                            "bytes */" + length
                        ); // Required in 416.
                        response.sendError(
                            HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE
                        );
                        return true;
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        // Prepare and initialize response --------------------------------------------------------

        // Get content type by file name and set content disposition.
        String disposition = "inline";

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null) {
            contentType = "application/octet-stream";
        } else if (!contentType.startsWith("image")) {
            // Else, expect for images, determine content disposition. If content type is supported by
            // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
            String accept = request.getHeader("Accept");
            disposition =
                accept != null && HttpUtils.accepts(accept, contentType)
                    ? "inline"
                    : "attachment";
        }
        log.debug("Content-Type : {}", contentType);
        // Initialize response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader(
            "Content-Disposition",
            disposition +
            ";filename=\"" +
            URLEncoder.encode(
                new String(fileName.getBytes(), StandardCharsets.ISO_8859_1),
                StandardCharsets.ISO_8859_1
            ) +
            "\""
        );
        log.debug("Content-Disposition : {}", disposition);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader(
            "ETag",
            URLEncoder.encode(
                new String(fileName.getBytes(), StandardCharsets.ISO_8859_1),
                StandardCharsets.ISO_8859_1
            )
        );
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader(
            "Expires",
            System.currentTimeMillis() + DEFAULT_EXPIRE_TIME
        );

        // Send requested file (part(s)) to client ------------------------------------------------

        // Prepare streams.
        try (
            RandomAccessFile input = new RandomAccessFile(this.file, "r");
            ServletOutputStream output = response.getOutputStream()
        ) {
            if (ranges.isEmpty() || ranges.get(0) == full) {
                // Return full file.
                log.info("Return full file");
                response.setContentType(contentType);
                response.setHeader(
                    "Content-Range",
                    "bytes " + full.start + "-" + full.end + "/" + full.total
                );
                response.setHeader(
                    "Content-Length",
                    String.valueOf(full.length)
                );
                Range.copy(input, output, length, full.start, full.length);
            } else if (ranges.size() == 1) {
                // Return single part of file.
                Range r = ranges.get(0);
                log.info(
                    "Return 1 part of file : from ({}) to ({})",
                    r.start,
                    r.end
                );
                response.setContentType(contentType);
                response.setHeader(
                    "Content-Range",
                    "bytes " + r.start + "-" + r.end + "/" + r.total
                );
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
                // Copy single part range.
                Range.copy(input, output, length, r.start, r.length);
            } else {
                // Return multiple parts of file.
                response.setContentType(
                    "multipart/byteranges; boundary=" + MULTIPART_BOUNDARY
                );
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Cast back to ServletOutputStream to get the easy println methods.

                // Copy multi part range.
                for (Range r : ranges) {
                    log.info(
                        "Return multi part of file : from ({}) to ({})",
                        r.start,
                        r.end
                    );
                    // Add multipart boundary and header fields for every range.
                    output.println();
                    output.println("--" + MULTIPART_BOUNDARY);
                    output.println("Content-Type: " + contentType);
                    output.println(
                        "Content-Range: bytes " +
                        r.start +
                        "-" +
                        r.end +
                        "/" +
                        r.total
                    );
                    // Copy single part range of multi part range.
                    Range.copy(input, output, length, r.start, r.length);
                }

                // End with multipart boundary.
                output.println();
                output.println("--" + MULTIPART_BOUNDARY + "--");
            }
        }
        return true;
    }

    private static class Range {

        final long start;
        final long end;
        final long length;
        final long total;

        /**
         * Construct a byte range.
         *
         * @param start Start of the byte range.
         * @param end   End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

        public static long sublong(String value, int beginIndex, int endIndex) {
            String substring = value.substring(beginIndex, endIndex);
            return (substring.length() > 0) ? Long.parseLong(substring) : -1;
        }

        private static void copy(
            RandomAccessFile input,
            OutputStream output,
            long inputSize,
            long start,
            long length
        ) throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            if (inputSize == length) {
                // Write full range.
                while ((read = input.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                }
            } else {
                input.seek(start);
                long toRead = length;

                while ((read = input.read(buffer)) > 0) {
                    if ((toRead -= read) > 0) {
                        output.write(buffer, 0, read);
                        output.flush();
                    } else {
                        output.write(buffer, 0, (int) toRead + read);
                        output.flush();
                        break;
                    }
                }
            }
        }
    }

    private static class HttpUtils {

        /**
         * Returns true if the given accept header accepts the given value.
         *
         * @param acceptHeader The accept header.
         * @param toAccept     The value to be accepted.
         * @return True if the given accept header accepts the given value.
         */
        public static boolean accepts(String acceptHeader, String toAccept) {
            String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
            Arrays.sort(acceptValues);

            return (
                Arrays.binarySearch(acceptValues, toAccept) > -1 ||
                Arrays.binarySearch(
                    acceptValues,
                    toAccept.replaceAll("/.*$", "/*")
                ) >
                -1 ||
                Arrays.binarySearch(acceptValues, "*/*") > -1
            );
        }

        /**
         * Returns true if the given match header matches the given value.
         *
         * @param matchHeader The match header.
         * @param toMatch     The value to be matched.
         * @return True if the given match header matches the given value.
         */
        public static boolean matches(String matchHeader, String toMatch) {
            String[] matchValues = matchHeader.split("\\s*,\\s*");
            Arrays.sort(matchValues);
            return (
                Arrays.binarySearch(matchValues, toMatch) > -1 ||
                Arrays.binarySearch(matchValues, "*") > -1
            );
        }
    }
}
