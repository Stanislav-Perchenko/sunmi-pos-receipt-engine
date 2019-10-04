package com.alperez.sunmi.pos.receiptengine.template;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class ImageTemplateItem extends BaseTemplateItem {
    private final TextAlign horizontalAlignment;
    private final int width;
    private final int height;
    private final byte[] imageData;

    private Bitmap bitmap;

    private final String json;

    ImageTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        horizontalAlignment = TextAlign.fromJson(jObj.optString("horizontal_align", TextAlign.ALIGN_LEFT.getJsonValue()));
        width = Integer.parseInt(valueMapper.mapTextValue(jObj.getString("width")));
        height = Integer.parseInt(valueMapper.mapTextValue(jObj.getString("height")));
        imageData = valueMapper.mapByteArrayValue(jObj.getString("data_base64"));


    }

    public byte[] getImageData() {
        return imageData;
    }

    public String getJson() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageTemplateItem that = (ImageTemplateItem) o;
        return Arrays.equals(imageData, that.imageData);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(imageData);
    }

    static final String TYPE_JSON_VALUE = "image";

    @Override
    public String getTypeJsonValue() {
        return TYPE_JSON_VALUE;
    }

    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) {
        List<byte[]> ret = new LinkedList<>();
        if (printerParams.isUnidirectionPrintSupported()) ret.add(ESCUtils.setUnidirectionalPrintModeEnabled(true));

        ret.add(ESCUtils.setTextAlignment(horizontalAlignment));
        ret.add(ESCUtils.printBitmap(getBitmap(), 0));

        if (printerParams.isUnidirectionPrintSupported()) ret.add(ESCUtils.setUnidirectionalPrintModeEnabled(false));
        return ret;
    }


    private Bitmap getBitmap() {
        if (this.bitmap == null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            if (bmp.getWidth() == this.width && bmp.getHeight() == this.height) {
                this.bitmap = bmp;
            } else {
                this.bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                Canvas canv = new Canvas(this.bitmap);
                canv.drawBitmap(bmp, 0, 0, new Paint());
            }
        }
        return this.bitmap;
    }
}
