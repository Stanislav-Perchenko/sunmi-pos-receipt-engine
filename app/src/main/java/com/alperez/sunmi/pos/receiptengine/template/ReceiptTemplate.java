package com.alperez.sunmi.pos.receiptengine.template;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ReceiptTemplate {

    private PrintMode printMode;
    private Charset charset;
    private List<ITemplateItem> templateItems;

    private ReceiptTemplate() {}

    public PrintMode getPrintMode() {
        return printMode;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<ITemplateItem> getTemplateItems() {
        return templateItems;
    }

    public Collection<byte[]> getPrinterSetupCode() {
        List<byte[]> data = new LinkedList<>();
        if (charset.isMultiByte()) {
            data.add(ESCUtils.setCharsetTypeMultiByte());
            data.add(ESCUtils.setCodeSystemMultiByte(charset.getSunmiCode()));
        } else {
            data.add(ESCUtils.setCharsetTypeSingleByte());
            data.add(ESCUtils.setCodeSystemSingleByte(charset.getSunmiCode()));
        }
        return data;
    }

    public static ReceiptTemplate fromJson(JSONObject jObj, ParameterValueMapper valueMapper) throws JSONException {
        ReceiptTemplate tmpl = new ReceiptTemplate();
        tmpl.printMode = PrintMode.fromJson(jObj.getString("print_mode"));
        tmpl.charset = Charset.valueOf(jObj.getString("charset"));
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
