package com.alperez.sunmi.pos.receiptengine.template;

import android.text.TextUtils;

import org.json.JSONException;

public enum  TextAlign {

    ALIGN_LEFT("left"), ALIGN_CENTER("center"), ALIGN_RIGHT("right");

    private final String jsonValue;

    TextAlign(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public static TextAlign fromJson(String jsonValue) throws JSONException {
        for (TextAlign v : TextAlign.values()) {
            if (TextUtils.equals(v.jsonValue, jsonValue)) return v;
        }
        throw new JSONException("jsonValue for the TextAlign is not supported - "+jsonValue);
    }
}
