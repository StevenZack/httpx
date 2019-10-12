package com.xchat.stevenzack.langenius.httpx;

public interface HttpxHandler {
    void handle(HttpxResponseWriter w, HttpxRequest r);
}
