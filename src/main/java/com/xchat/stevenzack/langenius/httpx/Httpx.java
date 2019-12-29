package com.xchat.stevenzack.langenius.httpx;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    class RollingWindow{
        private int size;
        private List<Integer> list = new ArrayList<>();
        private RollingWindow(){}

        public RollingWindow(int size) {
            this.size = size;
        }
        public int getSize(){
            return size;
        }

        public void push(int i) {
            list.add(i);
            if (list.size() > size) {
                list.remove(0);
            }
        }
        public String toString(){
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) bytes[i] = list.get(i).byteValue();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
