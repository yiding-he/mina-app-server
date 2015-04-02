package com.hyd.appserver.utils;

/**
 * (description)
 *
 * @author yiding.he
 */
public class Bytes {

    final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static String toString(byte[] bytes) {
        StringBuilder tmp = new StringBuilder();
        for (byte b : bytes) {
            int shift = 4;
            int i = 0xFF & b;
            char[] buf = new char[32];
            int charPos = 32;
            int radix = 1 << shift;
            int mask = radix - 1;
            do {
                buf[--charPos] = digits[i & mask];
                i >>>= shift;
            } while (i != 0);

            if (charPos == 31) {
                tmp.append("0");
            }
            tmp.append(buf, charPos, (32 - charPos));
        }
        return tmp.toString();
    }
}
