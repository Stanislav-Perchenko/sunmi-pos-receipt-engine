package com.alperez.sunmi.pos.receiptengine.template;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class PriceFormattingTests {

    @Test
    public void validateFormatPriceFromLongAndScale() {
        assertThat(TextUtils.formatPrice(16435, 2, false)).isEqualTo("164.35");
        assertThat(TextUtils.formatPrice(16435, 1, false)).isEqualTo("1643.5");
        assertThat(TextUtils.formatPrice(4578569, 3, false)).isEqualTo("4578.569");
        assertThat(TextUtils.formatPrice(16435, 2, true)).isEqualTo("164.35");
        assertThat(TextUtils.formatPrice(16435, 1, true)).isEqualTo("1,643.5");
        assertThat(TextUtils.formatPrice(4578569, 3, true)).isEqualTo("4,578.569");
    }

    @Test
    public void validateFormatPriceFromValueAndScale() {
        assertThat(TextUtils.formatPrice(164.35000, 2, false)).isEqualTo("164.35");
        assertThat(TextUtils.formatPrice(1643.54575, 1, false)).isEqualTo("1643.5");
        assertThat(TextUtils.formatPrice(4578.56900, 3, false)).isEqualTo("4578.569");
        assertThat(TextUtils.formatPrice(164.354545, 2, true)).isEqualTo("164.35");
        assertThat(TextUtils.formatPrice(1643.5500, 1, true)).isEqualTo("1,643.6");
        assertThat(TextUtils.formatPrice(4578.5699111, 3, true)).isEqualTo("4,578.570");
    }

    @Test
    public void validateBuildValueFromLongAndScale() {
        assertThat(Math.abs(TextUtils.buildPriceValue(254356, 3) - 254.356)).isLessThan(0.000000001);
    }

}
