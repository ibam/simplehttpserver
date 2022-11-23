package io.github.ibam.fun.httpserver.handlers;

import io.github.ibam.fun.httpserver.SimpleHttpRequest;
import io.github.ibam.fun.httpserver.SimpleHttpResponse;

public interface MethodHandler {
    boolean canHandle(SimpleHttpRequest request);

    SimpleHttpResponse respond(SimpleHttpRequest request);
}
