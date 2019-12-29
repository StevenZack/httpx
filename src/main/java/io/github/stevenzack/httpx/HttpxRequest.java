package io.github.stevenzack.httpx;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpxRequest {
    public String method;
    public String uri;
    public String version;
    public String host;
    public HttpxURL httpxURL;
    public HttpxResponseWriter w;
    private Map<String, String> headers = new HashMap<>();
    //private
    private String body="";
    private BufferedReader br;
    private boolean alreadyReadBody = false;
    private String boundary;
    private long contentLength=-1;
    private boolean multipartReachEnd =false;

    public HttpxRequest(String method, String url, String body) throws Exception {
        this.method=method;
        this.alreadyReadBody = true;
        this.body=body;
        httpxURL = HttpxURL.parse(url);
        this.host = httpxURL.addr;
        this.uri = httpxURL.uri;
    }
    private HttpxRequest(){
    }

    public static HttpxRequest parseRequest(Socket socket) throws Exception {
        HttpxRequest r = new HttpxRequest();
        HttpxResponseWriter w = HttpxResponseWriter.parseRequestWriterx(socket);
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
        if (getContentLength() == 0) {
            return body;
        }
        try {
            int b;
            while ((b = br.read()) != -1) {
                body += ((char) b) + "";
                if (body.length() >= getContentLength()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public void saveBodyToFile(String dst)throws Exception {
        File file = new File(dst);
        if (file.isDirectory()) {
            throw new Exception("dst " + dst + " is not a file");
        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        PrintWriter printWriter = new PrintWriter(new FileWriter(file));
        if (alreadyReadBody) {
            printWriter.write(body);
            printWriter.flush();
            printWriter.close();
            return;
        }
        alreadyReadBody = true;
        if (getContentLength() == 0) {
            printWriter.close();
            return;
        }
        int b;
        int readLength = 0;
        while ((b = br.read()) != -1) {
            printWriter.write(b);
            readLength++;
            if (readLength >= getContentLength()) {
                break;
            }
        }
        printWriter.flush();
        printWriter.close();
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

    public HttpxMultipartForm readMultipart() throws IOException {
        HttpxMultipartForm x = new HttpxMultipartForm(br, getBoundary(), new HttpxMultipartForm.ReachTheEnd() {
            @Override
            public void setEnd(boolean b) {
                multipartReachEnd = b;
            }
        });
        String boundary = getBoundary();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.equals(boundary + "--")) {
                multipartReachEnd = true;
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
    public boolean isMultipartEnd() {
        return multipartReachEnd;
    }
}
