package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
abstract class TextTemplateItem extends BaseTemplateItem {


    private final boolean allCaps;
    private final boolean bold;
    private final TextAlign textAlign;
    private final int scaleWidth;
    private final int scaleHeight;

    private final String json;

    public abstract boolean isMultiLine();

    TextTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        allCaps = jObj.optBoolean("all_caps", false);
        bold = jObj.optBoolean("bold", false);
        textAlign = TextAlign.fromJson(jObj.optString("text_align", TextAlign.ALIGN_LEFT.getJsonValue()));
        scaleWidth  = jObj.optInt("scale_w", 0);
        scaleHeight = jObj.optInt("scale_h", 0);
    }

    public boolean isAllCaps() {
        return allCaps;
    }

    public boolean isBold() {
        return bold;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextTemplateItem that = (TextTemplateItem) o;
        return allCaps == that.allCaps &&
                bold == that.bold &&
                scaleWidth == that.scaleWidth &&
                scaleHeight == that.scaleHeight &&
                textAlign == that.textAlign &&
                isMultiLine() == that.isMultiLine();
    }

    @Override
    public int hashCode() {
        return Objects.hash(allCaps, bold, textAlign, scaleWidth, scaleHeight, isMultiLine());
    }




    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        Collection<byte[]> dataset = new LinkedList<>();

        dataset.add(ESCUtils.setTextAlignment(getTextAlign()));
        dataset.add(ESCUtils.setBoldEnabled(isBold()));

        int scW = getScaleWidth();
        if (scW < printerParams.characterScaleWidthLimits()[0]) scW = printerParams.characterScaleWidthLimits()[0];
        else if (scW > printerParams.characterScaleWidthLimits()[1]) scW = printerParams.characterScaleWidthLimits()[1];
        int scH = getScaleHeight();
        if (scH < printerParams.characterScaleHeightLimits()[0]) scH = printerParams.characterScaleHeightLimits()[0];
        else if (scH > printerParams.characterScaleHeightLimits()[1]) scH = printerParams.characterScaleHeightLimits()[1];
        dataset.add(ESCUtils.setCharacterScale(scW, scH));

        return dataset;
    }
}
