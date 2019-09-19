package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.sunmi.printerhelper.receipt.parammapper.ParameterValueMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public abstract class TextTemplateItem extends BaseTemplateItem {

    private final String textValue;
    private final boolean isAllCaps;
    private final boolean isBold;
    private final TextAlign textAlign;
    private final int scaleWidth;
    private final int scaleHeight;

    private final String json;

    public abstract boolean isMultiLine();

    public TextTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        textValue = valueMapper.mapTextValue(jObj.getString("text"));
        isAllCaps = jObj.optBoolean("all_caps", false);
        isBold = jObj.optBoolean("bold", false);
        textAlign = TextAlign.fromJson(jObj.optString("text_align", TextAlign.ALIGN_LEFT.getJsonValue()));
        scaleWidth  = jObj.optInt("scale_w", 0);
        scaleHeight = jObj.optInt("scale_h", 0);
    }

    public String getTextValue() {
        return textValue;
    }

    public boolean isAllCaps() {
        return isAllCaps;
    }

    public boolean isBold() {
        return isBold;
    }

    public TextAlign getTextAlign() {
        return textAlign;
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
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextTemplateItem that = (TextTemplateItem) o;
        return isAllCaps == that.isAllCaps &&
                isBold == that.isBold &&
                scaleWidth == that.scaleWidth &&
                scaleHeight == that.scaleHeight &&
                textValue.equals(that.textValue) &&
                textAlign == that.textAlign &&
                isMultiLine() == that.isMultiLine();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(textValue, isAllCaps, isBold, textAlign, scaleWidth, scaleHeight, isMultiLine());
    }
}
