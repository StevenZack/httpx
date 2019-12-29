import com.xchat.stevenzack.langenius.httpx.*;

import java.io.File;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
        try {
            HttpxServer server = new HttpxServer();
            server.handleFile("/", "/home/asd/go/src/index.html");
            server.addPrehandler(new HttpxHandler() {
                @Override
                public void handle(HttpxResponseWriter w, HttpxRequest r) {
                    System.out.println(r.uri);
                }
            });
            server.handleFunc("/upload", new HttpxHandler() {
                @Override
                public void handle(HttpxResponseWriter w, HttpxRequest r) {
                    try {
                        r.saveBodyToFile("post.txt");
                        w.writeString("OK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            server.listenAndServe(8080);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
