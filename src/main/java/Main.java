import io.github.gofaith.jywjl.httpx.*;

public class Main {
    public static void main(String[] args) {
        HttpxServer s = new HttpxServer();
        s.handleFunc("/", new HttpxHandler() {
            @Override
            public void handle(HttpxResponseWriter w, HttpxRequest r) {
                System.out.println(r.uri);
                while (r.isMultipartNotEnd()) {
                    try {
                        HttpxMultipartForm x = r.readMultipart();
                        if (x.isFile()) {
                            System.out.println("file:"+x.filename);
                            x.saveFile(x.filename);
                            continue;
                        }
                        System.out.println(x.name+":");
                        String value = x.getValue();
                        System.out.println(value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                w.writeString("ok");
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
