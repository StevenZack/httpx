package io.github.stevenzack.httpx;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpxClient {
    private Socket socket;
    public HttpxResponse doReq(HttpxRequest request) throws Exception {
        socket = new Socket(request.httpxURL.host, request.httpxURL.port);

        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        pw.println(request.method+" "+request.httpxURL.uri+" HTTP/1.1");
        pw.println("Host: "+request.host);
        pw.println("User-Agent: httpx/0.0.1");
        pw.println("Content-Length: "+ request.getBody().length());
        pw.println("Accept: */*");
        pw.println();
        pw.flush();
        pw.println(request.getBody());
        pw.println();
        pw.println();
        pw.flush();

        HttpxResponse response = HttpxResponse.readResponse(socket);
        socket.close();
        return response;
    }
}
