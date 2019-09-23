package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.Nullable;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public interface ITemplateItem {

    String getTypeJsonValue();

    String getJson();

    Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException;

    @Nullable
    static ITemplateItem optFromJson(JSONObject jObj, ParameterValueMapper valueMapper) throws JSONException {
        final String type = jObj.getString("type");
        switch (type) {
            case ImageTemplateItem.TYPE_JSON_VALUE:
                return new ImageTemplateItem(jObj, valueMapper);
            case MultiLineTextTemplateItem.TYPE_JSON_VALUE:
                return new MultiLineTextTemplateItem(jObj, valueMapper);
            case SingleLineTextTemplateItem.TYPE_JSON_VALUE:
                return new SingleLineTextTemplateItem(jObj, valueMapper);
            case SingleValueTemplateItem.TYPE_JSON_VALUE:
                return new SingleValueTemplateItem(jObj, valueMapper);
            case TableTemplateItem.TYPE_JSON_VALUE:
                return new TableTemplateItem(jObj, valueMapper);
            default:
                return null;
        }
    }
}
