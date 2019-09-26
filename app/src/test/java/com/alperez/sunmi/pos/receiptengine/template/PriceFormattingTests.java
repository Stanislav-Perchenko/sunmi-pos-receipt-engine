package com.alperez.sunmi.pos.receiptengine.template;

import org.junit.Test;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;

public class PriceFormattingTests {

    @Test
    public void validateFormatPriceFromLongAndScale() {
        Locale loc = new Locale("en", "GB", "");
        assertThat(TextUtils.formatPrice(16435, 2, true, loc)).isEqualTo("\u00A3164.35");
        assertThat(TextUtils.formatPrice(16435, 1, true, loc)).isEqualTo("\u00A31,643.50");
        assertThat(TextUtils.formatPrice(4578569, 3, true, loc)).isEqualTo("\u00A34,578.57");
        assertThat(TextUtils.formatPrice(35464578563L, 3, true, loc)).isEqualTo("\u00A335,464,578.56");

        assertThat(TextUtils.formatPrice(16435, 2, false, loc)).isEqualTo("164.35");
        assertThat(TextUtils.formatPrice(16435, 1, false, loc)).isEqualTo("1643.5");
        assertThat(TextUtils.formatPrice(4578569, 3, false, loc)).isEqualTo("4578.569");

        loc = new Locale("en", "US", "");
        assertThat(TextUtils.formatPrice(16435, 2, true, loc)).isEqualTo("$164.35");
        assertThat(TextUtils.formatPrice(16435, 1, true, loc)).isEqualTo("$1,643.50");
        assertThat(TextUtils.formatPrice(4578569, 3, true, loc)).isEqualTo("$4,578.57");
        assertThat(TextUtils.formatPrice(5464578566L, 3, true, loc)).isEqualTo("$5,464,578.57");



    }



    @Test
    public void validateBuildValueFromLongAndScale() {
        assertThat(Math.abs(TextUtils.buildPriceValue(254356, 3) - 254.356)).isLessThan(0.000000001);
    }

}
