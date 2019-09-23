package com.alperez.sunmi.pos.receiptengine.template;

import android.text.TextUtils;

import org.json.JSONException;

public enum  TextAlign {

    ALIGN_LEFT("left", (byte) 48), ALIGN_CENTER("center", (byte) 49), ALIGN_RIGHT("right", (byte) 50);

    private final String jsonValue;
    private final byte escPosValue;

    TextAlign(String jsonValue, byte escPosValue) {
        this.jsonValue = jsonValue;
        this.escPosValue = escPosValue;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public byte getEscPosValue() {
        return escPosValue;
    }

    public static TextAlign fromJson(String jsonValue) throws JSONException {
        for (TextAlign v : TextAlign.values()) {
            if (TextUtils.equals(v.jsonValue, jsonValue)) return v;
        }
        throw new JSONException("jsonValue for the TextAlign is not supported - "+jsonValue);
    }
}
