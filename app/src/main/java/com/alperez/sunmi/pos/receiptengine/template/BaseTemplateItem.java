package com.alperez.sunmi.pos.receiptengine.template;

import org.json.JSONException;
import org.json.JSONObject;

abstract class BaseTemplateItem implements ITemplateItem {

    BaseTemplateItem(JSONObject jObj) throws JSONException {
        if (!jObj.getString("type").equals(getTypeJsonValue())) {
            throw new JSONException(String.format("Wrong type - %s, expected - %s", jObj.getString("type"), getTypeJsonValue()));
        }
    }
}
