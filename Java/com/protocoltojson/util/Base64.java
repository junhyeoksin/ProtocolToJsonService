package com.protocoltojson.util;

public class Base64 {

    public static String encode(String str) {
        return encode(str.getBytes());
    }

    public static String encode(byte[] raw) {
        StringBuffer encoded = new StringBuffer();
        int length = raw.length;
        char[] base64 = new char[4];
        for(int i=0; i < length; i+=3) {
            int block = 0;
            int slack = length - i - 1;
            int end = (slack >= 2) ? 2 : slack;
            for(int j=0; j <= end; j++) {
                byte b = raw[i + j];
                int neuter = (b < 0) ? b + 256 : b;
                block += neuter << (8 * (2 - j));
            }
            for(int j=0; j < 4; j++) {
                int sixbit = (block >>> (6 * (3 - j))) & 0x3F;
                base64[j] = getChar(sixbit);
            }
            if( slack < 1 )  base64[2] = '=';
            if( slack < 2 )  base64[3] = '=';
            encoded.append(base64);
        }
        return encoded.toString();
    }

    protected static char getChar(int sixBit) {
        if( sixBit >= 0 && sixBit <= 25 )
            return (char)('A' + sixBit);
        if( sixBit >= 26 && sixBit <= 51 )
            return (char)('a' + (sixBit-26));
        if( sixBit >= 52 && sixBit <= 61 )
            return (char)('0' + (sixBit-52));
        if( sixBit == 62 ) return '+';
        if( sixBit == 63 ) return '/';
        return '?';
    }

    public static byte[] decode(String base64) {
        int pad = 0;
        for(int i=base64.length()-1; base64.charAt(i) == '='; i--)
            pad++;
        int length = base64.length() * 6 / 8 - pad;
        byte[] raw = new byte[length];
        int rawIndex = 0;
        for(int i=0; i < base64.length(); i+=4) {
            int block = (getValue(base64.charAt(i)) << 18)
                    + (getValue(base64.charAt(i + 1)) << 12)
                    + (getValue(base64.charAt(i + 2)) << 6)
                    + (getValue(base64.charAt(i + 3)));
            for (int j = 0; j < 3 && rawIndex + j <raw.length; j++)
                raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xFF);
            rawIndex += 3;
        }
        return raw;
    }

    protected static int getValue(char c) {
        if( c >= 'A' && c <= 'Z' )
            return c-'A';
        if( c >= 'a' && c <= 'z' )
            return c-'a' + 26;
        if( c >= '0' && c <= '9' )
            return c-'0' + 52;
        if( c == '+' ) return 62;
        if( c == '/' ) return 63;
        if( c == '=' ) return 0;
        return -1;
    }

}
