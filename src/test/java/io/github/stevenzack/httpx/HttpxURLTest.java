package io.github.stevenzack.httpx;

import org.junit.Test;

public class HttpxURLTest {

    @Test
    public void parse() {
        try {
            HttpxURL url = HttpxURL.parse("http://localhost:8080/12/a?12=1");
            System.out.println(url.scheme);
            System.out.println(url.addr);
            System.out.println(url.host);
            System.out.println(url.port);
            System.out.println(url.uri  );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}