package com.alperez.sunmi.pos.receiptengine.template;

import com.sunmi.printerhelper.receipt.parammapper.ParameterValueMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ReceiptTemplate {

    private PrintMode printMode;
    private List<ITemplateItem> templateItems;

    private ReceiptTemplate() {}

    public PrintMode getPrintMode() {
        return printMode;
    }

    public List<ITemplateItem> getTemplateItems() {
        return templateItems;
    }

    public static ReceiptTemplate fromJson(JSONObject jObj, ParameterValueMapper valueMapper) throws JSONException {
        ReceiptTemplate tmpl = new ReceiptTemplate();
        tmpl.printMode = PrintMode.fromJson(jObj.getString("print_mode"));
        JSONArray jItems = jObj.getJSONArray("data");
        List<ITemplateItem> items = new LinkedList<>();
        for (int i=0; i<jItems.length(); i++) {
            ITemplateItem item = ITemplateItem.optFromJson(jItems.getJSONObject(i), valueMapper);
            if (item == null) {
                continue;
            }
            items.add(item);
        }
        tmpl.templateItems = Collections.unmodifiableList(items);
        return tmpl;
    }
}
