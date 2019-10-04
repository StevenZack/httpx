package io.github.gofaith.jywjl.httpx;

public class Httpx {
    public static final String methodGet = "GET";
    public static final String methodPost = "POST";

    public static char[] rangeChars(char[] chars, int length) {
        char[] out = new char[length];
        for (int i = 0; i < length; i++) {
            out[i] = chars[i];
        }
        return out;
    }
}
