import com.xchat.stevenzack.langenius.httpx.Httpx;
import com.xchat.stevenzack.langenius.httpx.HttpxClient;
import com.xchat.stevenzack.langenius.httpx.HttpxRequest;
import com.xchat.stevenzack.langenius.httpx.HttpxResponse;
import io.github.gofaith.jywjl.httpx.*;

public class Main {
    public static void main(String[] args) {
        try {
            HttpxClient client = new HttpxClient();
            HttpxRequest r = new HttpxRequest(Httpx.methodGet, "http://localhost:8080/a.txt", "");
            HttpxResponse rp = client.doReq(r);
            System.out.println(rp.contentLength);
            System.out.println(rp.body);
            System.out.println(rp.body.length());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
