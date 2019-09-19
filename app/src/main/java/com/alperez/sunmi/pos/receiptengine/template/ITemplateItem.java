package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.Nullable;

import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;

import org.json.JSONException;
import org.json.JSONObject;

public interface ITemplateItem {

    String getTypeJsonValue();

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
