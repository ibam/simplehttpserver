package io.github.ibam.fun.httpserver;

import java.util.*;

public class SimpleHttpRequest {

    private final SimpleHttpMethods requestMethod;
    private final String requestPath;
    private final String requestVersion;
    private final Map<String, Set<String>> headerFields;

    private byte[] entity = new byte[0];
    public SimpleHttpRequest(final String headerString) {
        final String[] headerLines = headerString.split("\n");
        final String[] firstHeaderLineTokens = headerLines[0].split(" ");

        requestMethod = SimpleHttpMethods.valueOf(firstHeaderLineTokens[0]);
        requestPath = firstHeaderLineTokens[1];
        requestVersion = firstHeaderLineTokens[2];

        headerFields = new HashMap<>();
        for (int i = 1; i < headerLines.length; i++) {
            final String[] headerLineTokens = headerLines[i].split(": ");
            headerFields.computeIfAbsent(headerLineTokens[0], (s) -> new HashSet<>())
                    .addAll(Arrays.asList(headerLineTokens[1]
                            .split(",")));
        }
    }

    public void setEntity(final byte[] entity) {
        this.entity = entity;
    }

    public byte[] getEntity() {
        return entity;
    }

    public boolean isKeepAlive() {
        return headerFields.getOrDefault("Connection", Collections.emptySet()).contains("keep-alive");
    }

    public SimpleHttpMethods getMethod() {
        return requestMethod;
    }

    public String getVersion() {
        return requestVersion;
    }

    public boolean headerHasEntity() {
        return !headerFields.getOrDefault("Content-Length", Collections.emptySet()).isEmpty();
    }

    private Optional<Integer> getPossibleHeaderValueAsInt(final String headerKey) {
        final Set<String> headerValues = headerFields.getOrDefault(headerKey, Collections.emptySet());
        if (headerValues.isEmpty()) {
            return Optional.empty();
        }

        for (String headerValue : headerValues) {
            try {
                return Optional.of(Integer.parseInt(headerValue));
            } catch (Exception ex) {
                // ignore
            }
        }

        return Optional.empty();
    }

    public Optional<Integer> getPossibleContentLength() {
        return getPossibleHeaderValueAsInt("Content-Length");
    }

    public enum SimpleHttpMethods {
        OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
    }
}
