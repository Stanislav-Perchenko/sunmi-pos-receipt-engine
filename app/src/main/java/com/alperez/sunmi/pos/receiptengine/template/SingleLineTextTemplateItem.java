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
import java.util.Objects;

final class SingleLineTextTemplateItem extends TextTemplateItem {

    private final String textValue;

    SingleLineTextTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj, valueMapper);
        textValue = TextUtils.removeAllChars(valueMapper.mapTextValue(jObj.getString("text")), '\n');
    }

    public String getTextValue() {
        return textValue;
    }

    @Override
    public boolean isMultiLine() {
        return false;
    }

    static final String TYPE_JSON_VALUE = "single_text";

    @Override
    public String getTypeJsonValue() {
        return TYPE_JSON_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SingleLineTextTemplateItem that = (SingleLineTextTemplateItem) o;
        return textValue.equals(that.textValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), textValue);
    }

    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        Collection<byte[]> dataset = super.getPrinterRawData(charset, printerParams);

        String run_str = isAllCaps() ? getTextValue().toUpperCase() : getTextValue();

        int sc_w = getScaleWidth();
        if (sc_w < printerParams.characterScaleWidthLimits()[0]) sc_w = printerParams.characterScaleWidthLimits()[0];
        else if (sc_w > printerParams.characterScaleWidthLimits()[1]) sc_w = printerParams.characterScaleWidthLimits()[1];
        int maxLen = printerParams.lineLengthFromScaleWidth(sc_w);
        if(run_str.length() > maxLen) {
            run_str = run_str.substring(0, maxLen);
        }

        dataset.add(run_str.getBytes(charset.getEncodingStdName()));
        dataset.add(ESCUtils.nextLine(1));

        return dataset;
    }
}
