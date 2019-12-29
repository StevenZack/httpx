package io.github.stevenzack.httpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpxResponse {
    public String version,status;
    public Map<String, String> headers = new HashMap<>();
    public long contentLength;
    public String body;
    public static HttpxResponse readResponse(Socket socket) throws Exception {
        HttpxResponse response = new HttpxResponse();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        response.readHead(bufferedReader);
        bufferedReader.close();
        return  response;
    }

    private void readHead(BufferedReader bufferedReader) throws Exception {
        String line;
        boolean firstLineRead=false;
        while ((line = bufferedReader.readLine()) != null) {
            if (!firstLineRead) {
                readFirstLine(line);
                firstLineRead = true;
                continue;
            }

            if (line.equals("")) {
                break;
            }

            readHeader(line);
        }

        readBody(bufferedReader);
    }

    private void readHeader(String line)throws Exception {
        String[] ss = line.split(": ");
        if (ss.length < 2) {
            throw new Exception("Invalid format of http response: " + line);
        }

        headers.put(ss[0], ss[1]);

        if (ss[0].equals("Content-Length")) {
            contentLength=Long.parseLong(ss[1]);
        }
    }

    private void readFirstLine(String line) throws Exception {
        String[] ss = line.split(" ");
        if (ss.length != 3) {
            throw new Exception("Invalid format of http response:" + line);
        }
        version = ss[0];
        status = ss[1] + " "+ss[2];
    }

    private void readBody(BufferedReader bufferedReader) throws IOException {
        if (contentLength == 0) {
            body = "";
            return;
        }

        StringBuilder builder = new StringBuilder();
        long offset=0;
        char[] chars = new char[10240];
        while (offset < contentLength) {
            int length = bufferedReader.read(chars);
            if (length == 0) {
                break;
            }

            if (length > contentLength - offset) {
                length = (int) (contentLength - offset);
            }

            builder.append(Httpx.rangeChars(chars,length));
            offset += length;
        }

        body = builder.toString();
    }

}
