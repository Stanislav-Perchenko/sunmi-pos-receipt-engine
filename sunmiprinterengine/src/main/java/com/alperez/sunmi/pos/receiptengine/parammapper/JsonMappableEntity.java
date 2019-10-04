package com.alperez.sunmi.pos.receiptengine.parammapper;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonMappableEntity {

    private final String json;
    public JsonMappableEntity(@NonNull JSONObject jObj) {
        json = jObj.toString();
    }

    public JSONObject getOriginalJson() {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return json;
    }
}
