package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData() {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        List<byte[]> dataset = new LinkedList<>();


        String run_str = isAllCaps() ? getTextValue().toUpperCase() : getTextValue();

        /*if(isBold()) {
            dataset.add()
        } else {

        }*/




















        final int n = 4 + new Random().nextInt(6);
        for (int i=0; i<n; i++) dataset.add(new byte[0]);
        return dataset;
    }
}
