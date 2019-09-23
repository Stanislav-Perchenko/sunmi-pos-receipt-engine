package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

final class SingleValueTemplateItem extends BaseTemplateItem {

    private final String textName;
    private final boolean isNameBold;
    private final boolean isNameAllCaps;
    private final String textValue;
    private final boolean isValueBold;
    private final boolean isValueAllCaps;
    private final TextAlign valueTextAlign;
    private final int scaleWidth;
    private final int scaleHeight;

    private final String json;

    SingleValueTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        textName = jObj.getString("name");
        isNameBold = jObj.optBoolean("name_is_bold", false);
        isNameAllCaps = jObj.optBoolean("name_all_caps", false);
        textValue = valueMapper.mapTextValue(jObj.getString("value"));
        isValueBold = jObj.optBoolean("value_is_bold", false);
        isValueAllCaps = jObj.optBoolean("value_all_caps", false);
        valueTextAlign = TextAlign.fromJson(jObj.optString("value_align", TextAlign.ALIGN_LEFT.getJsonValue()));
        scaleWidth  = jObj.optInt("scale_w", 0);
        scaleHeight = jObj.optInt("scale_h", 0);
    }

    public String getTextName() {
        return textName;
    }

    public boolean isNameBold() {
        return isNameBold;
    }

    public boolean isNameAllCaps() {
        return isNameAllCaps;
    }

    public String getTextValue() {
        return textValue;
    }

    public boolean isValueBold() {
        return isValueBold;
    }

    public boolean isValueAllCaps() {
        return isValueAllCaps;
    }

    public TextAlign getValueTextAlign() {
        return valueTextAlign;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public String getJson() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleValueTemplateItem that = (SingleValueTemplateItem) o;
        return isNameBold == that.isNameBold &&
                isNameAllCaps == that.isNameAllCaps &&
                isValueBold == that.isValueBold &&
                isValueAllCaps == that.isValueAllCaps &&
                scaleWidth == that.scaleWidth &&
                scaleHeight == that.scaleHeight &&
                textName.equals(that.textName) &&
                textValue.equals(that.textValue) &&
                valueTextAlign == that.valueTextAlign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(textName, isNameBold, isNameAllCaps, textValue, isValueBold, isValueAllCaps, valueTextAlign, scaleWidth, scaleHeight);
    }

    static final String TYPE_JSON_VALUE = "single_value_item";

    @Override
    public String getTypeJsonValue() {
        return TYPE_JSON_VALUE;
    }

    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        List<byte[]> ret = new LinkedList<>();
        final int n = 4 + new Random().nextInt(6);
        for (int i=0; i<n; i++) ret.add(new byte[0]);
        return ret;
    }
}
