package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class MultiLineTextTemplateItem extends TextTemplateItem {

    MultiLineTextTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj, valueMapper);
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


    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData() {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        List<byte[]> ret = new LinkedList<>();
        final int n = 4 + new Random().nextInt(6);
        for (int i=0; i<n; i++) ret.add(new byte[0]);
        return ret;
    }
}
