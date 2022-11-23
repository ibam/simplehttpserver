package io.github.ibam.fun.httpserver.handlers;

import io.github.ibam.fun.httpserver.SimpleHttpRequest;
import io.github.ibam.fun.httpserver.SimpleHttpResponse;

import static io.github.ibam.fun.httpserver.HttpStatus.STATUS_200;

public class GetHandler implements MethodHandler {

    @Override
    public boolean canHandle(SimpleHttpRequest request) {
        return request.getMethod() == SimpleHttpRequest.SimpleHttpMethods.GET;
    }

    @Override
    public SimpleHttpResponse respond(final SimpleHttpRequest request) {
        return new SimpleHttpResponse(request, "Hello world!", STATUS_200);
    }
}
