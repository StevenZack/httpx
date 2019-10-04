package io.github.gofaith.jywjl.httpx;

public interface HttpxHandler {
    void handle(HttpxResponseWriter w, HttpxRequest r);
}
