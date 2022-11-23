package io.github.ibam.fun.httpserver;

import io.github.ibam.fun.httpserver.handlers.GetHandler;
import io.github.ibam.fun.httpserver.handlers.MethodHandler;
import io.github.ibam.fun.httpserver.handlers.PostHandler;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static io.github.ibam.fun.httpserver.SimpleHttpRequest.SimpleHttpMethods;

public class SimpleHttpHandler implements Runnable {

    public static final Charset HTTP_CHARSET = StandardCharsets.US_ASCII;

    private final Socket tcpSocket;
    private final ExecutorService parentExecutor;

    private final Collection<MethodHandler> handlers;

    public SimpleHttpHandler(final ExecutorService parentExecutor, final Socket tcpSocket) {
        this.parentExecutor = parentExecutor;
        this.tcpSocket = tcpSocket;

        handlers = Arrays.asList(new GetHandler(), new PostHandler());
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting to parse request from socket " + tcpSocket + " by thread #" + Thread.currentThread().threadId() + ", socket is" + (tcpSocket.isClosed() ? "" : " not") + " closed");

            final SimpleHttpRequest request = extractHttpRequest(tcpSocket.getInputStream());
            final SimpleHttpResponse response = handle(request);
            writeResponse(tcpSocket.getOutputStream(), response);

            // if we have finished handling the request but the request indicates that the connection is keep-alive,
            // re-queue the handler for future communications
            if (request.isKeepAlive() && !tcpSocket.isClosed()) {
                parentExecutor.submit(this);
            } else if (!tcpSocket.isClosed()) { // else the connection is not keep-alive, close the connection
                tcpSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Failed parsing request from socket " + tcpSocket + " by thread #" + Thread.currentThread().threadId());
            ex.printStackTrace();
        }
    }

    /**
     * Write the http response into the output stream and flush the stream
     * @param outputStream the output stream from the incoming socket
     * @param response the response
     * @throws IOException if there's any I/O issues when writing to the stream
     */
    private void writeResponse(final OutputStream outputStream, final SimpleHttpResponse response) throws IOException {
        writeLine(outputStream, response.getStatusLine());

        for (Map.Entry<String, Set<String>> headerEntry : response.getHeaders().entrySet()) {
            final String headerValue = String.join(",", headerEntry.getValue());
            writeLine(outputStream, headerEntry.getKey() + ": " + headerValue);
        }

        writeLine(outputStream);
        writeLine(outputStream, response.getBody());
        outputStream.flush();
    }

    /**
     * Write an empty newline to the output stream according to the default HTTP Charset
     * @param outputStream the output stream from the incoming socket
     * @throws IOException if there's any I/O issues when writing to the stream
     */
    private void writeLine(final OutputStream outputStream) throws IOException {
        writeLine(outputStream, "");
    }

    /**
     * Write the payload to the output stream according to the default HTTP Charset
     * @param outputStream the output stream from the incoming socket
     * @param payload the string
     * @throws IOException if there's any I/O issues when writing to the stream
     */
    private void writeLine(final OutputStream outputStream, final String payload) throws IOException {
        outputStream.write((payload + "\r\n").getBytes(HTTP_CHARSET));
    }

    private SimpleHttpResponse handle(final SimpleHttpRequest request) {
        for (MethodHandler handler : handlers) {
            if (handler.canHandle(request)) {
                return handler.respond(request);
            }
        }

        return SimpleHttpResponse.emptyResponse();
    }

    /**
     * This method will block until the input stream contains recognizable http request bytes
     *
     * @param inputStream the input stream for the http request
     * @return the http request
     * @throws IOException if I/O exceptions occur when reading the stream
     */
    private SimpleHttpRequest extractHttpRequest(final InputStream inputStream) throws IOException {
        final InputStreamReader streamReader = new InputStreamReader(inputStream, HTTP_CHARSET);
        final BufferedReader lineReader = new BufferedReader(streamReader);

        String requestLine = lineReader.readLine();

        if (requestLine == null) {
            throw new EOFException("End of stream has been reached, socket is possibly disconnected");
        }

        int requestStartIndex = -1;

        // keep on advancing until we found the start of the HTTP request
        while (requestLine != null && (requestStartIndex = seekHttpMethodStartIndex(requestLine)) < 0) {
            requestLine = lineReader.readLine();
        }

        // if we cannot find any http requests in the request stream, we throw an IO Exception
        if (requestLine == null) {
            throw new IOException("Cannot find http request in the input stream!");
        }

        // extract the first line according to the start index of the found http method
        final StringBuilder headerStringBuilder = new StringBuilder(requestLine.substring(requestStartIndex)).append("\n");

        // extract the rest of the headers until we found an empty line
        String line;
        while ((line = lineReader.readLine()) != null && !line.trim().isBlank()) {
            headerStringBuilder.append(line).append("\n");
        }

        final SimpleHttpRequest request = new SimpleHttpRequest(headerStringBuilder.toString());
        final Optional<Integer> possibleContentLength = request.getPossibleContentLength();

        // if the request contains an entity, consume the entity as much as the defined content length
        if (possibleContentLength.isPresent()) {
            final byte[] entityContent = new byte[possibleContentLength.get()];
            for (int i = 0; i < entityContent.length; i++) {
                entityContent[i] = (byte) lineReader.read();
            }
            request.setEntity(entityContent);
        }
        return request;
    }

    private int seekHttpMethodStartIndex(final String line) {
        if (line == null) {
            return -1;
        }

        // start with startsWith since it's more efficient and likely
        for (SimpleHttpMethods method : SimpleHttpMethods.values()) {
            if (line.startsWith(method.name())) {
                return 0;
            }
        }

        // continue with indexOf since it's more costly
        for (SimpleHttpMethods method : SimpleHttpMethods.values()) {
            int startIndex = -1;
            if ((startIndex = line.indexOf(method.name())) > 0) {
                return startIndex;
            }
        }

        return -1;
    }
}
