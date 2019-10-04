package com.alperez.sunmi.pos.receiptengine.escpos;

import android.graphics.Bitmap;

final class BytesUtil {

    /**
     * Convert the bitmap to a raster image with the first four digits wide and high
     */
    static byte[] getBytesFromBitMap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bw = (width - 1) / 8 + 1;

        byte[] rv = new byte[height * bw + 4];
        rv[0] = (byte) bw;//xL
        rv[1] = (byte) (bw >> 8);//xH
        rv[2] = (byte) height;
        rv[3] = (byte) (height >> 8);

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int clr = pixels[width * i + j];
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                byte gray = (RGB2Gray(red, green, blue));
                rv[bw*i + j/8 + 4] = (byte) (rv[bw*i + j/8 + 4] | (gray << (7 - j % 8)));
            }
        }

        return rv;
    }

    @SuppressWarnings("ConstantConditionalExpression")
    private static byte RGB2Gray(int r, int g, int b) {
        return (false ? ((int) (0.29900 * r + 0.58700 * g + 0.11400 * b) > 200)
                : ((int) (0.29900 * r + 0.58700 * g + 0.11400 * b) < 200)) ? (byte) 1 : (byte) 0;
    }


    //字节数组组合操作1
    static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private BytesUtil() { }
}
