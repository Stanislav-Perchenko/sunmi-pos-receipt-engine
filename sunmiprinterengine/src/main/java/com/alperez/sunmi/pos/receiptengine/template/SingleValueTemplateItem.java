package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

final class SingleValueTemplateItem extends BaseTemplateItem {

    private final String textName;//////////////////////////////////////////////////////////////////
    private final boolean isNameBold;///////////////////////////////////////////////////////////////
    private final boolean isNameAllCaps;////////////////////////////////////////////////////////////
    private final String textValue;/////////////////////////////////////////////////////////////////
    private final boolean isValueBold;//////////////////////////////////////////////////////////////
    private final boolean isValueAllCaps;///////////////////////////////////////////////////////////
    private final TextAlign valueTextAlign;/////////////////////////////////////////////////////////
    private final int scaleWidth;///////////////////////////////////////////////////////////////////
    private final int scaleHeight;//////////////////////////////////////////////////////////////////

    private final String json;

    SingleValueTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        textName = TextUtils.removeAllChars(jObj.getString("name"), '\n');
        isNameBold = jObj.optBoolean("name_is_bold", false);
        isNameAllCaps = jObj.optBoolean("name_all_caps", false);
        textValue = TextUtils.removeAllChars(valueMapper.mapTextValue(jObj.getString("value")), '\n');
        isValueBold = jObj.optBoolean("value_is_bold", false);
        isValueAllCaps = jObj.optBoolean("value_all_caps", false);
        TextAlign v_aling = TextAlign.fromJson(jObj.optString("value_align", TextAlign.ALIGN_LEFT.getJsonValue()));
        valueTextAlign = (v_aling == TextAlign.ALIGN_CENTER) ? TextAlign.ALIGN_LEFT : v_aling;
        scaleWidth  = jObj.optInt("scale_w", 0);
        scaleHeight = jObj.optInt("scale_h", 0);
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

        final String work_name = isNameAllCaps ? textName.toUpperCase() : textName;
        final String work_value = isValueAllCaps ? textValue.toUpperCase() : textValue;

        List<byte[]> dataset = new LinkedList<>();
        dataset.add(ESCUtils.setTextAlignment(TextAlign.ALIGN_LEFT));

        int scW = scaleWidth;
        if (scW < printerParams.characterScaleWidthLimits()[0]) scW = printerParams.characterScaleWidthLimits()[0];
        else if (scW > printerParams.characterScaleWidthLimits()[1]) scW = printerParams.characterScaleWidthLimits()[1];
        int scH = scaleHeight;
        if (scH < printerParams.characterScaleHeightLimits()[0]) scH = printerParams.characterScaleHeightLimits()[0];
        else if (scH > printerParams.characterScaleHeightLimits()[1]) scH = printerParams.characterScaleHeightLimits()[1];
        dataset.add(ESCUtils.setCharacterScale(scW, scH));
        final int maxLen = printerParams.lineLengthFromScaleWidth(scW);
        final int valueMaxLen = maxLen - work_name.length();
        if (valueMaxLen < 3) return new LinkedList<>(); //No print


        String[] value_lines = TextUtils.splitTextByLines(work_value, valueMaxLen);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(127);
        try {
            for (int i=0; i<value_lines.length; i++) {
                final int valueExtraChars = valueMaxLen - value_lines[i].length();
                if (i == 0) {
                    bos.write(ESCUtils.setLineSpacingDefault());
                    bos.write(ESCUtils.setBoldEnabled(isNameBold));
                    bos.write(work_name.getBytes(charset.getEncodingStdName()));
                    if (valueTextAlign == TextAlign.ALIGN_RIGHT && valueExtraChars > 0) {
                        TextUtils.writeNCharacters(bos, ' ', charset, valueExtraChars);
                    }
                    bos.write(ESCUtils.setBoldEnabled(isValueBold));
                } else {
                    int tabulation = work_name.length();
                    if (valueTextAlign == TextAlign.ALIGN_RIGHT) tabulation += valueExtraChars;
                    TextUtils.writeNCharacters(bos, ' ', charset, tabulation);
                    if (i == 1) bos.write(ESCUtils.setLineSpacing(printerParams.reducedLineSpacingValue()));
                }
                bos.write(value_lines[i].getBytes(charset.getEncodingStdName()));
                bos.write("\n".getBytes(charset.getEncodingStdName()));
            }
        } catch (IOException e) {/*Ignore - not happen by design*/}
        dataset.add(bos.toByteArray());
        return dataset;
    }


}
