package net.xicp.chocolatedisco.gatewayweb.filter.servlet.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class LoggingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private static final String JSON_CONTENT_TYPE = "application/json";

    private byte[] content;


    private final HttpServletRequest delegate;

    public LoggingHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.delegate = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (ArrayUtils.isEmpty(content)) {
            return delegate.getInputStream();
        }
        return new LoggingServletInputStream(content);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (ArrayUtils.isEmpty(content)) {
            return delegate.getReader();
        }
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String getContent() {
        try {
            String contentType = getContentType();
            if (contentType != null) {
                if (getContentType().equals(FORM_CONTENT_TYPE)) {
                    content = getParameterMap().entrySet().stream().map(e -> {
                        String[] value = e.getValue();
                        return e.getKey() + "=" + (value.length == 1 ? value[0] : Arrays.toString(value));
                    }).collect(Collectors.joining("&")).getBytes();
                } else if (getContentType().equals(JSON_CONTENT_TYPE)) {
                    content = IOUtils.toByteArray(delegate.getInputStream());
                }
            }
            if (content != null) {
                String requestEncoding = getCharacterEncoding();
                String normalizedContent = StringUtils.normalizeSpace(new String(content, requestEncoding != null ? requestEncoding : StandardCharsets.UTF_8.name()));
                return StringUtils.isBlank(normalizedContent) ? null : normalizedContent;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private class LoggingServletInputStream extends ServletInputStream {

        private final InputStream is;

        private LoggingServletInputStream(byte[] content) {
            this.is = new ByteArrayInputStream(content);
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() throws IOException {
            return this.is.read();
        }

        @Override
        public void close() throws IOException {
            super.close();
            is.close();
        }
    }
}
