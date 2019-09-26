package com.alperez.sunmi.pos.receiptengine.template;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

final class TextUtils {

    public static String reduceWitespacesBeforeLayout(String text, int maxLineLen) {
        final StringBuilder result = new StringBuilder();
        final int len = text.length();
        int start = -1;
        Character chStart = null;
        final char[] spaces = new char[maxLineLen];
        Arrays.fill(spaces, ' ');
        for (int index = 0; index<len; index++) {
            char ch = text.charAt(index);
            if (ch == ' ') {
                if (start < 0) {
                    start = index;
                    chStart = (index > 0) ? text.charAt(index-1) : null;
                }
            } else {
                if (start >= 0) {
                    final int sps_len = index - start;

                    if ((chStart == null) && (sps_len < maxLineLen)) {
                        result.append(spaces, 0, sps_len);
                    } else if (chStart != null) {

                        if (ch == '\n') {
                            // Omit
                        } else if (chStart == '\n') {
                            if (sps_len < maxLineLen) {
                                result.append(spaces, 0, sps_len);
                            } else {
                                result.append('\n');
                            }
                        } else {
                            result.append(' ');
                        }

                    }
                    start = -1;
                }//start >= 0

                result.append(ch);
            } // ch != ' '

        } //for
        return result.toString();
    }

    public static String[] splitTextByLines(String text, int maxLineLen) {
        final List<String> lines = new LinkedList<>();
        final int txt_len = text.length();
        int start = 0;
        boolean protectLeadingWhitespaces = true;
next_line:
        while (start < txt_len) {

            if(!protectLeadingWhitespaces) {
                while (text.charAt(start) == ' ') start ++;
            } else {
                protectLeadingWhitespaces = false;
            }

            int end = start;
            int positionSpace = -1;
            int positionSecondary = -1;
            while ((end < txt_len) && ((end - start) < maxLineLen )) {
                char ch = text.charAt(end);
                if (ch == '\n') {
                    //Early go next line and delete the \n character
                    lines.add(text.substring(start, end));
                    start = end + 1;
                    protectLeadingWhitespaces = true;
                    continue next_line;
                } else if (ch == ' ') {
                    positionSpace = end;
                } else if (isSecondaryDelimiter(ch)) {
                    positionSecondary = end;
                }
                end ++;
            }

            if (end >= txt_len) {
                lines.add(text.substring(start, txt_len));
                start = txt_len;
            } else if (text.charAt(end) == ' ') {//test 1-st character of the next line is space
                lines.add(text.substring(start, end));
                start = end;
            } else {
                boolean useSpace = false;
                boolean useSecondary = false;
                if ((positionSpace >= 0) && ((positionSpace - start) >= maxLineLen/3)) {
                    useSpace = true;
                } else if (positionSpace >= 0 && positionSecondary < positionSpace) {
                    useSpace = true;
                } else if (positionSecondary >= 0) {
                    useSecondary = true;
                }

                if (useSpace) {
                    lines.add(text.substring(start, positionSpace));
                    start = positionSpace;
                } else if (useSecondary) {
                    lines.add(text.substring(start, positionSecondary + 1));
                    start = positionSecondary + 1;
                } else {
                    lines.add(text.substring(start, end));
                    start = end;
                }

            }
        }
        if ((txt_len >= 2) && (text.charAt(txt_len-1) == '\n') && (text.charAt(txt_len-2) != '\n')) {
            lines.add("");
        }
        return lines.toArray(new String[lines.size()]);
    }



    private static final char[] SECONDARY_DELIMITERS = {'.', ',', ';', ':', '!', '?', '*', '/', '-', '+', '=' };
    static int lastIndexOfSecondaryDelimiter(CharSequence text) {
        final int len = text.length();
        for (int i=len-1; i>=0; i--) {
            char ch = text.charAt(i);
            for (char c : SECONDARY_DELIMITERS) {
                if (ch == c) return i;
            }
        }
        return -1;
    }

    static boolean isSecondaryDelimiter(char ch) {
        for (char c : SECONDARY_DELIMITERS) {
            if (ch == c) return true;
        }
        return false;
    }

    static void writeNCharacters(OutputStream os, char ch, Charset charset, int num) throws IOException {
        StringBuilder sb = new StringBuilder(num);
        for (int i=0; i<num; i++) {
            sb.append(ch);
        }
        os.write(sb.toString().getBytes(charset.getEncodingStdName()));
    }

    static String removeAllChars(CharSequence text, char toRemove) {
        StringBuilder sb = new StringBuilder(text.length());
        final int len = text.length();
        for (int i=0; i<len; i++) {
            char ch = text.charAt(i);
            if (ch != toRemove) sb.append(ch);
        }
        return sb.toString();
    }


    public static String formatPrice(long amount, int scale, boolean formatAsCurrency, Locale locale) {

        double value = amount;
        for (int i=0; i<scale; i++) value /= 10;

        if (formatAsCurrency) {
            return NumberFormat.getCurrencyInstance(locale).format(value);
        } else {
            return String.format("%."+scale+"f", value);
        }
    }

    //TODO Make Unit test for this
    public static double buildPriceValue(long amount, int scale) {
        double value = amount;
        for (int i=0; i<scale; i++) value /= 10;
        return value;
    }

    private TextUtils() { }
}
