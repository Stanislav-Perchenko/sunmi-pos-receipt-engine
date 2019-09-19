package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.sunmi.printerhelper.receipt.parammapper.ParameterValueMapper;

import org.json.JSONException;
import org.json.JSONObject;

public final class SingleLineTextTemplateItem extends TextTemplateItem {

    SingleLineTextTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj, valueMapper);
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

}
