package io.github.stevenzack.httpx;

public interface HttpxHandler {
    void handle(HttpxResponseWriter w, HttpxRequest r);
}
