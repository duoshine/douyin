package com.duoshine.douyin.meishe.sdkdemo.utils;

/**
 * @author：chu chenguang on 2020/6/16 16:15
 */

public class Base64Encoder {
    private static final char[] encodeMap = initEncodeMap();

    Base64Encoder() {
    }

    private static char[] initEncodeMap() {
        char[] map = new char[64];

        int i;
        for(i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }

        for(i = 26; i < 52; ++i) {
            map[i] = (char)(97 + (i - 26));
        }

        for(i = 52; i < 62; ++i) {
            map[i] = (char)(48 + (i - 52));
        }

        map[62] = '+';
        map[63] = '/';
        return map;
    }

    public static char encode(int i) {
        return encodeMap[i & 63];
    }

    public static byte encodeByte(int i) {
        return (byte)encodeMap[i & 63];
    }

    public static String print(byte[] input, int offset, int len) {
        char[] buf = new char[(len + 2) / 3 * 4];
        int ptr = print(input, offset, len, buf, 0);

        assert ptr == buf.length;

        return new String(buf);
    }

    public static int print(byte[] input, int offset, int len, char[] buf, int ptr) {
        for(int i = offset; i < len; i += 3) {
            switch(len - i) {
                case 1:
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 3) << 4);
                    buf[ptr++] = '=';
                    buf[ptr++] = '=';
                    break;
                case 2:
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
                    buf[ptr++] = encode((input[i + 1] & 15) << 2);
                    buf[ptr++] = '=';
                    break;
                default:
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
                    buf[ptr++] = encode((input[i + 1] & 15) << 2 | input[i + 2] >> 6 & 3);
                    buf[ptr++] = encode(input[i + 2] & 63);
            }
        }

        return ptr;
    }
}
