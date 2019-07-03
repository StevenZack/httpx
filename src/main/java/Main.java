import httpx.Handlerx;
import httpx.Httpx;
import httpx.RequestWriterx;
import httpx.Requestx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Httpx s = new Httpx();
        s.handleFunc("/", new Handlerx() {
            @Override
            public void handle(RequestWriterx w, Requestx r) {
                System.out.println(r.getBody());
                System.out.println("#"+r.getBody().length());
                w.writeString(r.getBody());
            }
        });
        try {
            System.out.println("waiting");
            s.listenAndServe(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
