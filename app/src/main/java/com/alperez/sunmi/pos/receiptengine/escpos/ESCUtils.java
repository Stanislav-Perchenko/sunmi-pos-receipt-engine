package com.alperez.sunmi.pos.receiptengine.escpos;

@SuppressWarnings("WeakerAccess")
public final class ESCUtils {

    public static final byte ESC = 0x1B;// Escape
    public static final byte FS =  0x1C;// Text delimiter
    public static final byte GS =  0x1D;// Group separator
    public static final byte LF =  0x0A;// Print and wrap (horizontal orientation)


    public static byte[] setCharsetTypeMultiByte() {
        byte[] result = new byte[2];
        result[0] = FS;
        result[1] = 0x26;
        return result;
    }

    public static byte[] setCharsetTypeSingleByte() {
        byte[] result = new byte[2];
        result[0] = FS;
        result[1] = 0x2E;
        return result;
    }

    public static byte[] setCodeSystemMultiByte(byte charset) {
        byte[] result = new byte[3];
        result[0] = FS;
        result[1] = 0x43;
        result[2] = charset;
        return result;
    }

    public static byte[] setCodeSystemSingleByte(byte charset) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x74;
        result[2] = charset;
        return result;
    }


    private ESCUtils() { }
}
