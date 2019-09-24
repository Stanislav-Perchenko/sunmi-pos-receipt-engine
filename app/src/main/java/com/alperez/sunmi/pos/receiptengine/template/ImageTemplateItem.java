package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class ImageTemplateItem extends BaseTemplateItem {
    private final byte[] imageData;

    private final String json;

    ImageTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        imageData = valueMapper.mapByteArrayValue(jObj.getString("src"));
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
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        List<byte[]> ret = new LinkedList<>();
        if (printerParams.isUnidirectionPrintSupported()) ret.add(ESCUtils.setUnidirectionalPrintModeEnabled(true));

        ret.add(ESCUtils.setTextAlignment(TextAlign.ALIGN_CENTER));
        ret.add("\n{image}\n\n".getBytes(charset.getEncodingStdName()));

        if (printerParams.isUnidirectionPrintSupported()) ret.add(ESCUtils.setUnidirectionalPrintModeEnabled(false));
        return ret;
    }
}
