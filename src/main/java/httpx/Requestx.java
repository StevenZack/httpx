package httpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Requestx {
    public String method;
    public String uri;
    public String version;
    public String host;
    public RequestWriterx w;
    public String body="";
    public Map<String, String> headers = new HashMap<>();
    //private
    private BufferedReader br;
    public static Requestx parseRequest(Socket socket) throws IOException {
        Requestx r = new Requestx();
        RequestWriterx w = RequestWriterx.parseRequestWriterx(socket);
        r.w = w;
        r.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //head
        String line;
        boolean headFinished = false;
        boolean firstLineRead = false;
        while ((line = r.br.readLine()) != null) {
            // handle gap
            if (line.equals("")) {
                if (headFinished) {
                    if (r.body.endsWith("\n")) {
                        r.body = r.body.substring(0, r.body.length() - 1);
                    }
                    break;
                }
                headFinished = true;
                if (r.method.equals("GET")) {
                    break;
                }
                continue;
            }

            // read body
            if (headFinished) {
                r.body += line;
                r.body += "\n";
                continue;
            }

            // read first line
            if (!firstLineRead) {
                String[] ss = line.split(" ");
                if (ss.length != 3) {
                    r.w.setStatus400BadRequest();
                    return null;
                }
                r.method = ss[0];
                r.uri = ss[1];
                r.version = ss[2];
                firstLineRead=true;
                continue;
            }

            //read headers
            String[] ss = line.split(": ");
            if (ss.length < 2) {
                r.w.setStatus400BadRequest();
                return null;
            }
            r.headers.put(ss[0], ss[1]);
        }
        return  r;
    }
}
