package io.github.stevenzack.httpx;

public class HttpxURL {
    public String scheme,host,addr,uri;
    public int port;
    public static HttpxURL parse(String url) throws Exception {
        HttpxURL httpxURL = new HttpxURL();
        httpxURL.scheme = parseScheme(url);
        httpxURL.addr = parseAddr(httpxURL.scheme, url);
        httpxURL.host = parseHost(httpxURL.addr);
        httpxURL.port = parsePort(httpxURL.host, httpxURL.addr);
        httpxURL.uri = parseUri(httpxURL.scheme, httpxURL.addr, url);
        return httpxURL;
    }

    private static String parseScheme(String url) throws Exception {
        if (!url.startsWith("http://")) {
            throw new Exception("parse URL [" + url + "] failed: invalid scheme");
        }
        return "http://";
    }

    private static String parseAddr(String scheme, String url) throws Exception {
        if (url.length() <= scheme.length()) {
            throw new Exception("parse URL [" + url + "] failed: invalid host");
        }
        String subs = url.substring(scheme.length());
        for (int i = 0; i < subs.length(); i++) {
            if (subs.substring(i, i + 1).equals("/")) {
                return subs.substring(0, i);
            }
        }
        return subs;
    }

    private static String parseHost(String addr) throws Exception{
        String[] ss = addr.split(":");
        if (ss.length == 2) {
            Integer.parseInt(ss[1]);
            return ss[0];
        }
        return addr;
    }

    private static int parsePort(String host, String addr) throws Exception{
        if (host.length() == addr.length()) {
            return 80;
        }
        if (addr.length() <= host.length() + 1) {
            throw new Exception("parse address [" + addr + "] failed");
        }
        String port = addr.substring(host.length() + 1);
        return Integer.parseInt(port);
    }

    private static String parseUri(String scheme, String addr,String url) throws Exception{
        if (url.length() < scheme.length() + addr.length()) {
            throw new Exception("parse URL [" + url + "] failed: invalid url");
        }
        if (url.length() == scheme.length() + addr.length()) {
            return "/";
        }
        String uri = url.substring(scheme.length() + addr.length());
        return  uri;
    }
}
