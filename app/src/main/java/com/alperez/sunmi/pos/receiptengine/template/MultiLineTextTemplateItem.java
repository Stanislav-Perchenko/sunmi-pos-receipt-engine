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
import java.util.Objects;

final class MultiLineTextTemplateItem extends TextTemplateItem {

    private final String textValue;

    MultiLineTextTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj, valueMapper);
        textValue = valueMapper.mapTextValue(jObj.getString("text"));
    }

    public String getTextValue() {
        return textValue;
    }

    @Override
    public boolean isMultiLine() {
        return true;
    }

    static final String TYPE_JSON_VALUE = "multiline_text";

    @Override
    public String getTypeJsonValue() {
        return TYPE_JSON_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MultiLineTextTemplateItem that = (MultiLineTextTemplateItem) o;
        return textValue.equals(that.textValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), textValue);
    }

    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {

        int sc_w = getScaleWidth();
        if (sc_w < printerParams.characterScaleWidthLimits()[0]) sc_w = printerParams.characterScaleWidthLimits()[0];
        else if (sc_w > printerParams.characterScaleWidthLimits()[1]) sc_w = printerParams.characterScaleWidthLimits()[1];
        int maxLen = printerParams.lineLengthFromScaleWidth(sc_w);


        final String run_str = TextUtils.reduceWitespacesBeforeLayout(isAllCaps() ? getTextValue().toUpperCase() : getTextValue(), maxLen);


        Collection<byte[]> dataset = super.getPrinterRawData(charset, printerParams);
        if (run_str.length() > maxLen) {
            String[] lines = TextUtils.splitTextByLines(run_str, maxLen);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(run_str.length()*2+10);
            for (String line : lines) {
                try {
                    bos.write(line.getBytes(charset.getEncodingStdName()));
                } catch (IOException e) {/*This will never happen by design*/}
                bos.write(ESCUtils.LF);
            }
            dataset.add(bos.toByteArray());
        } else {
            dataset.add(run_str.getBytes(charset.getEncodingStdName()));
        }

        return dataset;
    }




}
