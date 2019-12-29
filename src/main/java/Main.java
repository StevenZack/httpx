import com.xchat.stevenzack.langenius.httpx.*;

public class Main {
    public static void main(String[] args) {
        try {
            HttpxServer server = new HttpxServer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
