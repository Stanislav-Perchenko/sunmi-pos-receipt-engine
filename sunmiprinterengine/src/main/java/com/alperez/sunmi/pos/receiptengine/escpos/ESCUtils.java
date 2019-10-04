package com.alperez.sunmi.pos.receiptengine.escpos;

import com.alperez.sunmi.pos.receiptengine.template.TextAlign;

@SuppressWarnings("WeakerAccess")
public final class ESCUtils {

    public static final byte ESC = 0x1B;// Escape
    public static final byte FS =  0x1C;// Text delimiter
    public static final byte GS =  0x1D;// Group separator
    public static final byte LF =  0x0A;// Print and wrap (horizontal orientation)

    public static char[] toUnicodeCharacter(byte[] bbb) {
        char[] ccc = new char[bbb.length];
        for (int i=0; i<bbb.length; i++) {
            ccc[i] = (char) (bbb[i] & 0x000000FF);
        }
        return ccc;
    }

    /**
     * ESC '@'
     * @return
     */
    public static byte[] initializePrinter() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 0x40;
        return result;
    }

    /**
     * ESC '2'
     * Sets line spacing to the default value - 30 dots
     * @return
     */
    public static byte[] setLineSpacingDefault() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 0x32;
        return result;
    }

    /**
     * ESC '3' nDots
     * Sets line spacing to the requested value
     * @param nDots line spacing value [0..255]
     * @return
     */
    public static byte[] setLineSpacing(int nDots) {
        if (nDots < 0) nDots = 0;
        else if (nDots > 255) nDots = 255;
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x33;
        result[2] = (byte) (nDots & 0xFF);
        return result;
    }

    /**
     * ESC 'U' enabled
     * With this command, it is decided whether the print head should print in
     * only one or in both directions.
     * This is used for printing graphics for fine alignment
     * @param enabled true - unidirectional mode is enabled
     * @return
     */
    public static byte[] setUnidirectionalPrintModeEnabled(boolean enabled) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x55;
        result[2] = (byte)(enabled ? 1 : 0);
        return result;
    }

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

    /**
     * ESC 'a' n
     * Select text justification
     * @param align
     * @return
     */
    public static byte[] setTextAlignment(TextAlign align) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x61;
        result[2] = align.getEscPosValue();
        return result;
    }

    /**
     * ESC 'E' enabled
     * Turn emphasized mode on/off
     * @param enabled
     * @return
     */
    public static byte[] setBoldEnabled(boolean enabled) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x45;
        result[2] = (byte)(enabled ? 1 : 0);
        return result;
    }

    /**
     * Scales start with 1 !!!
     * @param scaleWidth width scale in range [1..8]
     * @param scaleHeight height scale in range [1..8]
     * @return
     */
    public static byte[] setCharacterScale(int scaleWidth, int scaleHeight) {
        int combinedScale = ((ensureCharScaleValue(scaleWidth) << 4) & 0xF0) | (ensureCharScaleValue(scaleHeight) & 0x0F);
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 0x21;
        result[2] = (byte) combinedScale;
        return result;
    }

    private static int ensureCharScaleValue(int sc) {
        if (sc < 1) return 1;
        else if (sc > 8) return 8;
        else return sc-1;
    }

    /**
     * Jump the specified number of lines
     */
    public static byte[] nextLine(int lineNum) {
        byte[] result = new byte[lineNum];
        for (int i = 0; i < lineNum; i++) result[i] = LF;
        return result;
    }

    private ESCUtils() { }
}
