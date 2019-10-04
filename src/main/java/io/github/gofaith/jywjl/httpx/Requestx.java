package io.github.gofaith.jywjl.httpx;

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
    private Map<String, String> headers = new HashMap<>();
    //private
    private String body="";
    private BufferedReader br;
    private boolean alreadyReadBody = false;
    private String boundary;
    private long contentLength=-1;
    private boolean multipartReadTheEnd=true;

    public Requestx(String method,String url,String body) throws Exception {
        this.method=method;
        this.alreadyReadBody = true;
        this.body=body;
        HttpxURL httpxURL = HttpxURL.parse(url);
        this.host = httpxURL.addr;
        this.uri = httpxURL.uri;
    }
    private Requestx(){
    }

    public static Requestx parseRequest(Socket socket) throws Exception {
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

    public long getContentLength() {
        if (contentLength > -1) {
            return contentLength;
        }
        if (!headers.containsKey("Content-Length")) {
            return 0;
        }
        String value = getHeader("Content-Length");
        contentLength = Long.parseLong(value);
        return contentLength;
    }
    public String getBody() {
        if (alreadyReadBody) {
            return body;
        }
        alreadyReadBody = true;
        long contentLength = getContentLength();
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

    public String getHeader(String key) {
        if (!headers.containsKey(key)) {
            return "";
        }
        String value = headers.get(key);
        if (value == null) {
            return "";
        }
        return value.split("; ")[0];
    }

    public String getBoundary(){
        if (boundary != null && !boundary.equals("")) {
            return boundary;
        }

        if (!headers.containsKey("Content-Type")) {
            return "";
        }
        String contentType = headers.get("Content-Type");
        String[] strs = contentType.split("; ");
        if (strs.length < 2) {
            return "";
        }
        String[] bds=strs[1].split("=");
        if (bds.length < 2) {
            return "";
        }
        boundary="--"+bds[1];
        return boundary;
    }

    public MultipartFormx readMultipart() throws IOException {
        MultipartFormx x = new MultipartFormx(br, getBoundary(), new MultipartFormx.ReachTheEnd() {
            @Override
            public void setEnd(boolean b) {
                multipartReadTheEnd = !b;
            }
        });
        String boundary = getBoundary();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.equals(boundary + "--")) {
                multipartReadTheEnd = true;
                break;
            }
            if (line.equals(boundary)) {
                continue;
            }
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Content-Disposition:")) {
                String[] ss = line.split(": ");
                if (ss.length != 2) {
                    w.setStatus400BadRequest();
                    return null;
                }
                String[] values=ss[1].split("; ");
                if (values.length < 2) {
                    w.setStatus400BadRequest();
                    return null;
                }
                x.contentDiposition = values[0];
                //name
                String[] namekv=values[1].split("=");
                if (namekv.length == 2&&namekv[0].equals("name")) {
                    x.name = HttpxServer.trimQuotationMarks(namekv[1]);
                }
                // filename
                if (values.length >= 3) {
                    String[] filenamekv=values[2].split("=");
                    if (filenamekv.length == 2 && filenamekv[0].equals("filename")) {
                        x.filename = HttpxServer.trimQuotationMarks(filenamekv[1]);
                    }
                }
                continue;
            }

            if (line.startsWith("Content-Type")) {
                String[] ss=line.split(": ");
                if (ss.length == 2 && ss[0].equals("Content-Type")) {
                    x.contentType = ss[1];
                }
            }
        }
        return  x;
    }
    public boolean isMultipartNotEnd() {
        return multipartReadTheEnd;
    }
}
