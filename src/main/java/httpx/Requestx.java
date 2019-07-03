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
    public Map<String, String> headers = new HashMap<>();
    //private
    private String body="";
    private BufferedReader br;
    private boolean alreadyReadBody = false;
    public static Requestx parseRequest(Socket socket) throws IOException {
        Requestx r = new Requestx();
        RequestWriterx w = RequestWriterx.parseRequestWriterx(socket);
        r.w = w;
        r.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //head
        String line;
        boolean firstLineRead = false;
        while ((line = r.br.readLine()) != null) {
            // handle gap
            if (line.equals("")) {
                    break;
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
    public String getBody() {
        if (alreadyReadBody) {
            return body;
        }
        alreadyReadBody = true;
        long contentLength = 0;
        String strContentLength="Content-Length";
        if (headers.containsKey(strContentLength)) {
            contentLength = Long.parseLong(headers.get(strContentLength));
        }
        if (contentLength == 0) {
            return body;
        }
        try {
            int b;
            while ((b = br.read()) != -1) {
                body += ((char) b) + "";
                if (body.length() >= contentLength) {
                    break;
                }
            }
            if (body.endsWith("\n")) {
                body = "changed";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }
}
