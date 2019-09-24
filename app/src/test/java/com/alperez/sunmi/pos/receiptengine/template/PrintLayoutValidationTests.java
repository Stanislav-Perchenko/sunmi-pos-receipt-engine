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


    @Test(timeout = 500)
    public void validate_splitTextByLines_with_newlines() {
        String[] sss = TextUtils.splitTextByLines("\n", 24);
        assertThat(sss.length).isEqualTo(1);
        for (String s : sss) assertThat(s).isEqualTo("");

        sss = TextUtils.splitTextByLines("\n\n", 24);
        assertThat(sss.length).isEqualTo(2);
        for (String s : sss) assertThat(s).isEqualTo("");

        sss = TextUtils.splitTextByLines("\n\n\n\n\n", 24);
        assertThat(sss.length).isEqualTo(5);
        for (String s : sss) assertThat(s).isEqualTo("");

        sss = TextUtils.splitTextByLines("\n#%^&?\n", 24);
        assertThat(sss.length).isEqualTo(3);
        assertThat(sss[0]).isEqualTo("");
        assertThat(sss[1]).isEqualTo("#%^&?");
        assertThat(sss[2]).isEqualTo("");

        sss = TextUtils.splitTextByLines("\n\n\nAs-ssss.\n\n    - Bbbbb;\n\n", 24);
        assertThat(sss.length).isEqualTo(7);
        assertThat(sss[0]).isEqualTo("");
        assertThat(sss[1]).isEqualTo("");
        assertThat(sss[2]).isEqualTo("");
        assertThat(sss[3]).isEqualTo("As-ssss.");
        assertThat(sss[4]).isEqualTo("");
        assertThat(sss[5]).isEqualTo("    - Bbbbb;");
        assertThat(sss[6]).isEqualTo("");

        sss = TextUtils.splitTextByLines("   - Aaaaaaaaaaa;\n   - Bbbbbbbbbbbbbb;\n   - Ccccccccccccccccccccccccc;\n   - Ddddddddddd-eeeeeeeeeeeeeeee.\n", 24);
        assertThat(sss.length).isEqualTo(8);
        assertThat(sss[0]).isEqualTo("   - Aaaaaaaaaaa;");
        assertThat(sss[1]).isEqualTo("   - Bbbbbbbbbbbbbb;");
        assertThat(sss[2]).isEqualTo("   -");
        assertThat(sss[3]).isEqualTo("Cccccccccccccccccccccccc");
        assertThat(sss[4]).isEqualTo("c;");
        assertThat(sss[5]).isEqualTo("   - Ddddddddddd-");
        assertThat(sss[6]).isEqualTo("eeeeeeeeeeeeeeee.");
        assertThat(sss[7]).isEqualTo("");
    }

    @Test(timeout = 500)
    public void validate_splitTextByLines_no_newlines_2() {
        String[] sss = TextUtils.splitTextByLines("Aaaa. Bbbbbbbbbbbbbbbbb: ccccccccccc.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("Aaaa. Bbbbbbbbbbbbbbbbb:");
        assertThat(sss[1]).isEqualTo("ccccccccccc.");

        sss = TextUtils.splitTextByLines("Aaaa. Bbbbbbbbbbbbbbbbbb: ccccccccccc.", 24);
        assertThat(sss.length).isEqualTo(3);
        assertThat(sss[0]).isEqualTo("Aaaa.");
        assertThat(sss[1]).isEqualTo("Bbbbbbbbbbbbbbbbbb:");
        assertThat(sss[2]).isEqualTo("ccccccccccc.");

        sss = TextUtils.splitTextByLines("Aaaa. Bbbbbbbbb-bbbbbbbb:   ccccccccccccccccccccccc.", 24);
        assertThat(sss.length).isEqualTo(3);
        assertThat(sss[0]).isEqualTo("Aaaa. Bbbbbbbbb-");
        assertThat(sss[1]).isEqualTo("bbbbbbbb:  ");
        assertThat(sss[2]).isEqualTo("ccccccccccccccccccccccc.");

    }


    @Test(timeout = 500)
    public void validate_splitTextByLines_no_newlines() {

        String[] sss = TextUtils.splitTextByLines("1aa bbb ccc.", 24);
        assertThat(sss.length).isEqualTo(1);
        assertThat(sss[0]).isEqualTo("1aa bbb ccc.");

        sss = TextUtils.splitTextByLines("2", 24);
        assertThat(sss.length).isEqualTo(1);
        assertThat(sss[0]).isEqualTo("2");

        sss = TextUtils.splitTextByLines("   - 3", 24);
        assertThat(sss.length).isEqualTo(1);
        assertThat(sss[0]).isEqualTo("   - 3");

        sss = TextUtils.splitTextByLines("   - 4aaaadddfffredcfred", 24);
        assertThat(sss.length).isEqualTo(1);
        assertThat(sss[0]).isEqualTo("   - 4aaaadddfffredcfred");

        sss = TextUtils.splitTextByLines("   - 5aaaadddfffredc.fred eee.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("   - 5aaaadddfffredc.");
        assertThat(sss[1]).isEqualTo("fred eee.");

        sss = TextUtils.splitTextByLines("   - 6aaaadddfffredc.fr.Eddd eee.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("   - 6aaaadddfffredc.fr.");
        assertThat(sss[1]).isEqualTo("Eddd eee.");

        sss = TextUtils.splitTextByLines("   - 7aaaadddfffredc.fr. Eddd eee.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("   - 7aaaadddfffredc.fr.");
        assertThat(sss[1]).isEqualTo("Eddd eee.");

        sss = TextUtils.splitTextByLines("   - 8aaaadddfffredc.fr.  Eddd eee.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("   - 8aaaadddfffredc.fr.");
        assertThat(sss[1]).isEqualTo("Eddd eee.");

        sss = TextUtils.splitTextByLines("   - 9aaaadddfffredc.Fre. Eddd eee.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("   - 9aaaadddfffredc.");
        assertThat(sss[1]).isEqualTo("Fre. Eddd eee.");

        sss = TextUtils.splitTextByLines("10aaaaaabbbbbbbccccccdddddeeeeefffff ggg.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("10aaaaaabbbbbbbccccccddd");
        assertThat(sss[1]).isEqualTo("ddeeeeefffff ggg.");

        sss = TextUtils.splitTextByLines("11aaaaaabbbbbbbccccccddd. ddeeeeefffff ggg.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("11aaaaaabbbbbbbccccccddd");
        assertThat(sss[1]).isEqualTo(". ddeeeeefffff ggg.");

        sss = TextUtils.splitTextByLines("12aaaaaabbbbbbbccccccddd ddeeeeefffff ggg.", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("12aaaaaabbbbbbbccccccddd");
        assertThat(sss[1]).isEqualTo("ddeeeeefffff ggg.");

        sss = TextUtils.splitTextByLines("   - 13aaadddfffredcfdfred eee.", 24);
        assertThat(sss.length).isEqualTo(3);
        assertThat(sss[0]).isEqualTo("   -");
        assertThat(sss[1]).isEqualTo("13aaadddfffredcfdfred");
        assertThat(sss[2]).isEqualTo("eee.");

        sss = TextUtils.splitTextByLines("   - 14asssddddeerrrrtgG dfrtg gghtr. Gfd", 24);
        assertThat(sss.length).isEqualTo(2);
        assertThat(sss[0]).isEqualTo("   - 14asssddddeerrrrtgG");
        assertThat(sss[1]).isEqualTo("dfrtg gghtr. Gfd");

        sss = TextUtils.splitTextByLines("   - 15asssddddeerrrrtgf? dfrtg gghtr. Gfd", 24);
        assertThat(sss.length).isEqualTo(3);
        assertThat(sss[0]).isEqualTo("   -");
        assertThat(sss[1]).isEqualTo("15asssddddeerrrrtgf?");
        assertThat(sss[2]).isEqualTo("dfrtg gghtr. Gfd");

    }
}
