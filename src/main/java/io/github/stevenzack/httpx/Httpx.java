package io.github.stevenzack.httpx;

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

    public static class RollingWindow{
        private int size;
        private List<Integer> list = new ArrayList<>();
        private RollingWindow(){}

        public RollingWindow(int size) {
            this.size = size;
        }
        public int getSize(){
            return size;
        }

        public int push(int i) {
            list.add(i);
            if (list.size() > size) {
                int first = list.get(0);
                list.remove(0);
                return first;
            }
            return -1;
        }
        public String getString(){
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) bytes[i] = list.get(i).byteValue();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
