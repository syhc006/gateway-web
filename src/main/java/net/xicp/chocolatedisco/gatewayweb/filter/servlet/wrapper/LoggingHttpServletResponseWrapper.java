package net.xicp.chocolatedisco.gatewayweb.filter.servlet.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class LoggingHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private static final String JSON_CONTENT_TYPE = "application/json";

    private final LoggingServletOutpuStream loggingServletOutpuStream = new LoggingServletOutpuStream();

    private final HttpServletResponse delegate;

    public LoggingHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        delegate = response;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return loggingServletOutpuStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(loggingServletOutpuStream.baos);
    }

    public String getContent() {
        String content = null;
        try {
            String contentType = getContentType();
            if (contentType != null && contentType.contains(JSON_CONTENT_TYPE)) {
                String responseEncoding = delegate.getCharacterEncoding();
                boolean gzip = Optional.ofNullable(getHeader("Content-Encoding")).map(e -> e.equals("gzip")).orElse(false);
                if (gzip) {
                    InputStream stream = new GZIPInputStream(new ByteArrayInputStream(loggingServletOutpuStream.baos.toByteArray()));
                    content = IOUtils.toString(stream, responseEncoding != null ? responseEncoding : UTF_8.name());
                } else {
                    content = loggingServletOutpuStream.baos.toString(responseEncoding != null ? responseEncoding : UTF_8.name());
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            return content;
        }
    }

    public byte[] getContentAsBytes() {
        return loggingServletOutpuStream.baos.toByteArray();
    }

    private class LoggingServletOutpuStream extends ServletOutputStream {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }

        @Override
        public void write(int b) throws IOException {
            baos.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            baos.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            baos.write(b, off, len);
        }
    }
}
