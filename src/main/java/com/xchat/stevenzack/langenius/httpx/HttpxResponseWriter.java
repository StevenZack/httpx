package com.xchat.stevenzack.langenius.httpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpxResponseWriter {
    private PrintWriter pw;
    private String version = "HTTP/1.1";
    private String status = "200 OK";
    private String contentType = "text/plain";
    private long contentLength=-1;
    private String body = "";
    private BufferedReader br;
    private Map<String, String> header = new HashMap<>();
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

    public void fromReader(BufferedReader bufferedReader) {
        br = bufferedReader;
    }

    public void setHeader(String k, String v) {
        header.put(k, v);
    }

    public void setContentType(String s) {
        contentType = s;
    }

    public void setContentLength(long l) {
        contentLength = l;
    }

    public void finish()throws Exception {
        pw.println(version + " " + status);
        for (Map.Entry e: header.entrySet()) {
            pw.println(e.getKey()+": "+e.getValue());
        }
        pw.println("Content-Type: " + contentType);
        if (contentLength == -1) {
            pw.println("Content-Length: "+body.length());
        }else{
            pw.println("Content-Length: "+contentLength);
        }
        pw.println();
        if (br == null) {
            pw.print(body);
        }else{
            int b;
            int readLength=0;
            while ((b = br.read()) != -1) {
                pw.write(b);
                readLength++;
                if (readLength >= contentLength) {
                    break;
                }
            }
        }
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
