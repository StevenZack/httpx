package io.github.gofaith.jywjl.httpx;

import java.io.IOException;
import java.net.Socket;

public class HttpxClient {
    private Socket socket;
    public HttpxResponse doReq(HttpxRequest request) throws IOException {
        socket = new Socket(request.httpxURL.host, request.httpxURL.port);

    }
}
