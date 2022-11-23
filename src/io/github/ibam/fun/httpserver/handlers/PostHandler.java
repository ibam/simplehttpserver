package io.github.ibam.fun.httpserver.handlers;

import io.github.ibam.fun.httpserver.SimpleHttpRequest;
import io.github.ibam.fun.httpserver.SimpleHttpResponse;

import java.util.Base64;

import static io.github.ibam.fun.httpserver.HttpStatus.STATUS_200;

public class PostHandler implements MethodHandler {

    @Override
    public boolean canHandle(SimpleHttpRequest request) {
        return request.getMethod() == SimpleHttpRequest.SimpleHttpMethods.POST;
    }

    @Override
    public SimpleHttpResponse respond(final SimpleHttpRequest request) {
        final String encodedBody = Base64.getEncoder().encodeToString(request.getEntity());
        return new SimpleHttpResponse(request, encodedBody, STATUS_200);
    }
}
