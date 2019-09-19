package com.alperez.sunmi.pos.receiptengine.parammapper;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public interface ParameterValueMapper {

    @NonNull
    String mapTextValue(@NonNull String template) throws JSONException;

    @NonNull
    byte[] mapByteArrayValue(@NonNull String template);

    static ParameterValueMapper getInstance(JSONObject dataJson) {
        return new ParamMapperImpl(dataJson);
    }
}
