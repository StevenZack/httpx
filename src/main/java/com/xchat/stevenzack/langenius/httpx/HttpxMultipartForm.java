package com.xchat.stevenzack.langenius.httpx;

import java.io.*;

public class HttpxMultipartForm {
    public String contentDiposition;
    public String name;
    public String filename;
    public String contentType;

    private String value;
    private final String boundary;
    private final BufferedReader br;
    private final ReachTheEnd reachTheEnd;
    public HttpxMultipartForm(BufferedReader br, String boundary, ReachTheEnd reachTheEnd) {
        this.br=br;
        this.boundary = boundary;
        this.reachTheEnd = reachTheEnd;
    }

    public boolean isFile() {
        return filename != null && !filename.equals("");
    }
    public boolean isValue() {
        return filename == null || filename.equals("");
    }
    interface ReachTheEnd {
        void setEnd(boolean b);
    }
    public String getValue() throws IOException {
        if (value != null) {
            return value;
        }
        String str =null;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.equals(boundary + "--")) {
                reachTheEnd.setEnd(true);
                break;
            }
            if (line.equals(boundary)) {
                break;
            }
            if (str == null) {
                str = "";
            }
            str += line + "\n";
        }
        if (str == null) {
            return "";
        }
        if (str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        }
        value = str;
        return value;
    }

    public void saveFile(String dst) throws Exception {
        File file = new File(dst);
        if (file.isDirectory()) {
            throw new Exception("file " + dst + " is a dir");
        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        String line,lastLine = null;
        while ((line = br.readLine()) != null) {
            if (line.equals(boundary + "--")) {
                if (lastLine != null) {
                    pw.print(lastLine);
                }
                reachTheEnd.setEnd(true);
                break;
            }
            if (line.equals(boundary)) {
                if (lastLine != null) {
                    pw.print(lastLine);
                }
                break;
            }
            if (lastLine == null) {
                lastLine = line;
                continue;
            }
            pw.println(lastLine);
            lastLine = line;
        }
        pw.close();
        System.out.println("saved:"+file.getAbsolutePath());
    }
}
