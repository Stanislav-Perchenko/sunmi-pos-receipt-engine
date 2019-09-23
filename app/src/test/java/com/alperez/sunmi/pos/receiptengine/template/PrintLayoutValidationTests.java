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
    @Test
    public void validateTextMultilineLayout_var1() {
        String[] sss = TextUtils.splitTextByLines("Aaaaaa bbbbbbbb ccccc dddddddd eeeee fffffffffffffff   ggggggggggggg   hhhhhhhh ttttt hh.Aaaaaaaaaaaaaaaaaa bbbb.", 24);
        assertThat(sss.length).isEqualTo(6);
        assertThat(sss[0]).isEqualTo("Aaaaaa bbbbbbbb ccccc");
        assertThat(sss[1]).isEqualTo("dddddddd eeeee");
        assertThat(sss[2]).isEqualTo("fffffffffffffff  ");
        assertThat(sss[3]).isEqualTo("ggggggggggggg   hhhhhhhh");
        assertThat(sss[4]).isEqualTo("ttttt hh.");
        assertThat(sss[5]).isEqualTo("Aaaaaaaaaaaaaaaaaa bbbb.");
    }
}
