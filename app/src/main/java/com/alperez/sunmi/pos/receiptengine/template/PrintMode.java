package com.alperez.sunmi.pos.receiptengine.template;

import android.text.TextUtils;

import org.json.JSONException;

public enum PrintMode {
    MODE_TEXT("text"), MODE_GRAPHIC("graphic");


    private final String jsonValue;

    PrintMode(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public static PrintMode fromJson(String jsonValue) throws JSONException {
        for (PrintMode v : PrintMode.values()) {
            if (TextUtils.equals(v.jsonValue, jsonValue)) return v;
        }
        throw new JSONException("jsonValue for the PrintMode is not supported - "+jsonValue);
    }
}
