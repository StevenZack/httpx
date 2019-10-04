package io.github.gofaith.jywjl.httpx;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpxResponseWriter {
    private PrintWriter pw;
    private String version = "HTTP/1.1";
    private String status = "200 OK";
    private String contentType = "text/plain";
    private String body = "";
    public static HttpxResponseWriter parseRequestWriterx(Socket socket) throws IOException {
        HttpxResponseWriter w = new HttpxResponseWriter();
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        w.pw = pw;
        return  w;
    }

    public void writeString(String body) {
        if (status != null && !status.equals("200 OK")) {
            return;
        }
        this.body += body;
    }

    public void finish() {
        pw.print(version + " " + status);
        br();
        pw.print("Content-Type: " + contentType);
        br();
        pw.print("Content-Length: "+String.valueOf(body.length()));
        br();
        br();
        pw.print(body);
        pw.flush();
    }
    private void br() {
        pw.print("\n");
    }
    public void setStatus400BadRequest() {
        status = "400 Bad Request";
        body = status;
    }

    public void setStatus404NotFound() {
        status = "404 Not Found";
        body = status;
    }
}
