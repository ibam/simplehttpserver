package io.github.ibam.fun.httpserver;

import java.util.*;

public class SimpleHttpResponse {

    private static final SimpleHttpResponse EMPTY_RESPONSE = new SimpleHttpResponse(null,null, null);

    private final String responseBody;
    private final String statusLine;
    private final Map<String, Set<String>> headers;
    public SimpleHttpResponse(final SimpleHttpRequest request, final String body, final HttpStatus status) {
        this.responseBody = body;
        this.statusLine = constructStatusLine(request, status);
        this.headers = constructHeaders(request, body);
    }

    private Map<String, Set<String>> constructHeaders(final SimpleHttpRequest request, final String body) {
        if (request == null || body == null) {
            return Collections.emptyMap();
        }

        final Map<String, Set<String>> headerMap = new HashMap<>();
        headerMap.put("Content-Length", Collections.singleton("" + body.length()));
        return headerMap;
    }

    public Map<String, Set<String>> getHeaders() {
        return this.headers;
    }

    private String constructStatusLine(final SimpleHttpRequest request, final HttpStatus status) {
        if (request == null || status == null) {
            return null;
        }
        return request.getVersion() + " " + status.getStatusCode() + " " + status.getReasonPhrase() + " ";
    }

    public static SimpleHttpResponse emptyResponse() {
        return EMPTY_RESPONSE;
    }

    public String getBody() {
        return responseBody;
    }

    public String getStatusLine() {
        return statusLine;
    }
}
