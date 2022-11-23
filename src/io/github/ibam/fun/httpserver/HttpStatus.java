package io.github.ibam.fun.httpserver;

public enum HttpStatus {
    STATUS_100("Continue"),
    STATUS_101("Switching Protocols"),
    STATUS_200("OK"),
    STATUS_201("Created"),
    STATUS_202("Accepted"),
    STATUS_203("Non-Authoritative Information"),
    STATUS_204("No Content"),
    STATUS_205("Reset Content"),
    STATUS_206("Partial Content"),
    STATUS_300("Multiple Choices"),
    STATUS_301("Moved Permanently"),
    STATUS_302("Found"),
    STATUS_303("See Other"),
    STATUS_304("Not Modified"),
    STATUS_305("Use Proxy"),
    STATUS_307("Temporary Redirect"),
    STATUS_400("Bad Request"),
    STATUS_401("Unauthorized"),
    STATUS_402("Payment Required"),
    STATUS_403("Forbidden"),
    STATUS_404("Not Found"),
    STATUS_405("Method Not Allowed"),
    STATUS_406("Not Acceptable"),
    STATUS_407("Proxy Authentication Required"),
    STATUS_408("Request Time-out"),
    STATUS_409("Conflict"),
    STATUS_410("Gone"),
    STATUS_411("Length Required"),
    STATUS_412("Precondition Failed"),
    STATUS_413("Request Entity Too Large"),
    STATUS_414("Request-URI Too Large"),
    STATUS_415("Unsupported Media Type"),
    STATUS_416("Requested range not satisfiable"),
    STATUS_417("Expectation Failed"),
    STATUS_500("Internal Server Error"),
    STATUS_501("Not Implemented"),
    STATUS_502("Bad Gateway"),
    STATUS_503("Service Unavailable"),
    STATUS_504("Gateway Time-out"),
    STATUS_505("HTTP Version not supported");

    private final String reasonPhrase;
    private final int statusCode;

    HttpStatus(final String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
        final String name = this.name();
        this.statusCode = Integer.parseInt(name.substring(name.indexOf("_") + 1));
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
