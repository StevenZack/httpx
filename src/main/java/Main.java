import io.github.stevenzack.httpx.*;

public class Main {
    public static void main(String[] args) {
        try {
            HttpxServer server = new HttpxServer();
            server.addPrehandler(new HttpxHandler() {
                @Override
                public void handle(HttpxResponseWriter w, HttpxRequest r) {
                    System.out.println(r.uri);
                }
            });
            server.handleFile("/", "/home/asd/go/src/index.html");
            server.handleFunc("/upload", new HttpxHandler() {
                @Override
                public void handle(HttpxResponseWriter w, HttpxRequest r) {
                    try {
                        while (!r.isMultipartEnd()) {
                            HttpxMultipartForm form = r.readMultipart();
                            if (form.isFile()) {
                                form.saveFile(form.filename);
                            }else{
                                System.out.println(form.name+":"+form.getValue());
                            }
                        }
                        w.writeString("OK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            server.listenAndServe(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
