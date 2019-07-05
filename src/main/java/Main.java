import httpx.Handlerx;
import httpx.Httpx;
import httpx.RequestWriterx;
import httpx.Requestx;

public class Main {
    public static void main(String[] args) {
        Httpx s = new Httpx();
        s.handleFunc("/", new Handlerx() {
            @Override
            public void handle(RequestWriterx w, Requestx r) {
                w.writeString(r.getBoundary());
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
