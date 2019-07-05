import httpx.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Httpx s = new Httpx();
        s.handleFunc("/", new Handlerx() {
            @Override
            public void handle(RequestWriterx w, Requestx r) {
                System.out.println(r.uri);
                while (r.isMultipartNotEnd()) {
                    try {
                        MultipartFormx x = r.readMultipart();
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
