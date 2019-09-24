package com.alperez.sunmi.pos.receiptengine.template;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public final class PrintLayoutValidationTests {


    private static final int REDUCE_CASE_LINE_LEN = 8;
    private static final String[][] REDUCE_CASES = {
            {"1aaaa   bbbbb, ccccc  : ddddddd .", "1aaaa bbbbb, ccccc : ddddddd ."},
            {"     - 2aaa.     ", "     - 2aaa."},
            {"         3aaa bbb. ", "3aaa bbb."},
            {" 3-1aaa. ", " 3-1aaa."},
            {"4aaaaa\nbbbbb\n     \nccccc\n \ndddd", "4aaaaa\nbbbbb\n\nccccc\n\ndddd"},
            {"5aaaaa \nbbbbbbbb   \nccccc.", "5aaaaa\nbbbbbbbb\nccccc."},
            {"6aaaaa   \n    bbbbbbb    \n        ccccccccc   \n          ddd   eee.", "6aaaaa\n    bbbbbbb\n\nccccccccc\n\nddd eee."}};

    @Test
    public void validateReduceWhitespacesBeforeLayout() {
        for (String[] testCase : REDUCE_CASES) {
            String result = TextUtils.reduceWitespacesBeforeLayout(testCase[0], REDUCE_CASE_LINE_LEN);
            assertThat(result).isEqualTo(testCase[1]);
        }
    }

    @Test
    public void validateLastIndexSecondaryDelimiter() {
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("-")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter(" !? ")).isEqualTo(2);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("aaa.bbb;cd")).isEqualTo(7);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("aaa.bbb ;cd  ")).isEqualTo(8);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("aaa.bbb;cd+ ")).isEqualTo(10);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("cgjhdfkjghsdfkd sdjkfhsdkf")).isLessThan(0);
    }

    @Test
    public void validateSecondaryDelimiters() {
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter(".")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter(",")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter(";")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter(":")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("!")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("?")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("*")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("/")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("-")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("+")).isEqualTo(0);
        assertThat(TextUtils.lastIndexOfSecondaryDelimiter("=")).isEqualTo(0);
    }

    @Test
    public void validateChar_isSecondaryDelimiter() {
        assertThat(TextUtils.isSecondaryDelimiter('.')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter(',')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter(';')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter(':')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('!')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('?')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('*')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('/')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('-')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('+')).isTrue();
        assertThat(TextUtils.isSecondaryDelimiter('=')).isTrue();
        for (char ch : new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' ','0','1','2','3','4','5','6','7','8','9','@','#','$','%','^','(',')','{','}','[',']','<','>','\\'}) {
            assertThat(TextUtils.isSecondaryDelimiter(ch)).isFalse();
        }
    }

    /**
     * Aaaaaa bbbbbbbb ccccc dddddddd eeeee fffffffffffffff   ggggggggggggg   hhhhhhhh ttttt hh.Aaaaaaaaaaaaaaaaaa bbbb.
     * ------------------------
     * Aaaaaa bbbbbbbb ccccc
     * dddddddd eeeee
     * fffffffffffffff
     * ggggggggggggg   hhhhhhhh
     * ttttt hh.
     * Aaaaaaaaaaaaaaaaaa bbbb.
     */
    /*@Test
    public void validateTextMultilineLayout_var1() {
        String[] sss = TextUtils.splitTextByLines("Aaaaaa bbbbbbbb ccccc dddddddd eeeee fffffffffffffff   ggggggggggggg   hhhhhhhh ttttt hh.Aaaaaaaaaaaaaaaaaa bbbb.", 24);
        assertThat(sss.length).isEqualTo(6);
        assertThat(sss[0]).isEqualTo("Aaaaaa bbbbbbbb ccccc");
        assertThat(sss[1]).isEqualTo("dddddddd eeeee");
        assertThat(sss[2]).isEqualTo("fffffffffffffff  ");
        assertThat(sss[3]).isEqualTo("ggggggggggggg   hhhhhhhh");
        assertThat(sss[4]).isEqualTo("ttttt hh.");
        assertThat(sss[5]).isEqualTo("Aaaaaaaaaaaaaaaaaa bbbb.");
    }*/
}
