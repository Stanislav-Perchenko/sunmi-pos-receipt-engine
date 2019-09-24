package com.alperez.sunmi.pos.receiptengine.print;

public interface PosPrinterParams {
    boolean isUnidirectionPrintSupported();

    /**
     * An array of size 2 defines width scale limits
     * @return [0] - lower limit, [1] - upper limit
     */
    int[] characterScaleWidthLimits();

    /**
     * An array of size 2 defines height scale limits
     * @return [0] - lower limit, [1] - upper limit
     */
    int[] characterScaleHeightLimits();

    /**
     * Defiles length of printing lines with respect to the width scale of characters
     * @param scaleWidth value of scale starting with 1
     * @return
     */
    int lineLengthFromScaleWidth(int scaleWidth);
}
