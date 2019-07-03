package httpx;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestWriterx {
    private PrintWriter pw;
    private String version = "HTTP/1.1";
    private String status = "200 OK";
    private String contentType = "text/plain";
    private String body = "";
    public static RequestWriterx parseRequestWriterx(Socket socket) throws IOException {
        RequestWriterx w = new RequestWriterx();
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        w.pw = pw;
        return  w;
    }

    public void writeString(String body) {
        this.body += body;
    }

    public void finish() {
        pw.print(version + " " + status);
        br();
        pw.print("Content-Type:" + contentType);
        br();
        br();
        pw.print(body);
        br();
        br();
        pw.flush();
    }
    private void br() {
        pw.print("\r\n");
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
