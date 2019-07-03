package httpx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Httpx {
    private Map<String, Handlerx> r = new HashMap<>();
    private Map<String, Handlerx> mr = new HashMap<>();
    private List<Handlerx> pr = new ArrayList<>();
    private ServerSocket server;

    public void handleFunc(String uri, Handlerx handlerx) {
        r.put(uri, handlerx);
    }

    public void handleMultiReq(String uri, Handlerx handlerx) {
        mr.put(uri, handlerx);
    }

    public void addPrehandler(Handlerx handlerx) {
        pr.add(handlerx);
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
            Requestx r = Requestx.parseRequest(c);
            if (r == null) {
                return;
            }
            route(r);
            r.w.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void route(Requestx r) {
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

}
