package io.github.gofaith.jywjl.httpx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpxServer {
    private Map<String, HttpxHandler> r = new HashMap<>();
    private Map<String, HttpxHandler> mr = new HashMap<>();
    private List<HttpxHandler> pr = new ArrayList<>();
    private ServerSocket server;

    public void handleFunc(String uri, HttpxHandler httpxHandler) {
        r.put(uri, httpxHandler);
    }

    public void handleMultiReq(String uri, HttpxHandler httpxHandler) {
        mr.put(uri, httpxHandler);
    }

    public void addPrehandler(HttpxHandler httpxHandler) {
        pr.add(httpxHandler);
    }

    public void listenAndServe(int port) throws Exception{
        server = new ServerSocket(port);
        while (true) {
            final Socket c = server.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleConn(c);
                    try {
                        c.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void handleConn(Socket c)  {
        try {
            HttpxRequest r = HttpxRequest.parseRequest(c);
            if (r == null) {
                return;
            }
            route(r);
            r.w.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void route(HttpxRequest r) {
        //pr
        for (int i = 0; i < pr.size(); i++) {
            pr.get(i).handle(r.w,r);
        }
        //r
        if (!this.r.containsKey(r.uri)) {
            r.w.setStatus404NotFound();
            return;
        }
        this.r.get(r.uri).handle(r.w, r);
    }

    public static String trimQuotationMarks(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
