package com.alperez.sunmi.pos.receiptengine.template;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        text = text.trim();
        final List<String> lines = new LinkedList<>();
        while (text.length() > 0) {
            int primPosition = text.indexOf('\n');
            if (primPosition >= 0 && primPosition <= maxLineLen) {
                lines.add(text.substring(0, primPosition));
                text = (primPosition == (text.length() - 1)) ? "" : text.substring(primPosition+1);
                continue;
            }

            if (text.length() <= maxLineLen ) {
                lines.add(text);
                text = "";
                continue;
            }

            // text.length() > maxLineLen

            String rawLine = text.substring(0, maxLineLen);
            primPosition = rawLine.lastIndexOf(' ');
            boolean saveDelim = false;
            if (primPosition <= maxLineLen/2) {
                int secPos = lastIndexOfSecondaryDelimiter(rawLine);
                if (secPos > primPosition) {
                    primPosition = secPos;
                    saveDelim = true;
                }
            }
            if (primPosition < 0) {
                primPosition = maxLineLen;
                saveDelim = true;
            }

            lines.add(text.substring(0, primPosition));
            text = text.substring(saveDelim ? primPosition : (primPosition + 1));

        }
        return lines.toArray(new String[lines.size()]);
    }


    private static final char[] SECONDARY_DELIMITERS = {'.', ',', ';', ':', '!', '?', '*', '/', '-', '+', '=' };
    private static int lastIndexOfSecondaryDelimiter(CharSequence text) {
        final int len = text.length();
        for (int i=len-1; i>=0; i--) {
            char ch = text.charAt(i);
            for (char c : SECONDARY_DELIMITERS) {
                if (ch == c) return i;
            }
        }
        return -1;
    }

    private TextUtils() { }
}
