package com.alperez.sunmi.pos.receiptengine.template;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public final class ReceiptTemplate {

    private PrintMode printMode;
    private Charset charset;
    private boolean forceUnidirectionalPrintMode;

    private List<ITemplateItem> templateItems;

    private ReceiptTemplate() {}

    /*public PrintMode getPrintMode() {
        return printMode;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<ITemplateItem> getTemplateItems() {
        return templateItems;
    }*/

    private Collection<byte[]> getPrinterSetupCode(PosPrinterParams printerParams) {
        List<byte[]> data = new LinkedList<>();
        data.add(ESCUtils.initializePrinter());
        if (charset.isMultiByte()) {
            data.add(ESCUtils.setCharsetTypeMultiByte());
            data.add(ESCUtils.setCodeSystemMultiByte(charset.getSunmiCode()));
        } else {
            data.add(ESCUtils.setCharsetTypeSingleByte());
            data.add(ESCUtils.setCodeSystemSingleByte(charset.getSunmiCode()));
        }
        return data;
    }


    public Iterator<Collection<byte[]>> getRawPrintDataIterator(final PosPrinterParams printerParams) {
        return new Iterator<Collection<byte[]>>() {
            final int N = 1 + templateItems.size();
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < N;
            }

            @Override
            public Collection<byte[]> next() {
                try {
                    if (index == 0) {
                        return getPrinterSetupCode(printerParams);
                    } else if (hasNext()) {
                        return getTemplateItemPrintData(index-1, printerParams);
                    } else {
                        throw new NoSuchElementException("index = " + index);
                    }
                } catch (UnsupportedEncodingException e) {
                    String json = templateItems.get(index-1).getJson();
                    index = N;
                    throw new RuntimeException("Error getting print raw data from - "+json, e);
                } finally {
                    index++;
                }
            }
        };
    }

    private Collection<byte[]> getTemplateItemPrintData(int index, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        Collection<byte[]> origData = templateItems.get(index).getPrinterRawData(charset, printerParams);
        if (!forceUnidirectionalPrintMode) {
            return origData;
        }

        try {
            if (origData instanceof List) {
                ((List<byte[]>) origData).add(0, ESCUtils.setUnidirectionalPrintModeEnabled(true));
                return origData;
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (UnsupportedOperationException e) {
            List<byte[]> finData = new ArrayList<>(1+origData.size());
            finData.add(ESCUtils.setUnidirectionalPrintModeEnabled(true));
            finData.addAll(origData);
            return finData;
        }
    }


    public static ReceiptTemplate fromJson(JSONObject jObj, ParameterValueMapper valueMapper) throws JSONException {
        ReceiptTemplate tmpl = new ReceiptTemplate();
        tmpl.printMode = PrintMode.fromJson(jObj.getString("print_mode"));
        tmpl.charset = Charset.valueOf(jObj.getString("charset"));
        tmpl.forceUnidirectionalPrintMode = jObj.optBoolean("force_unidir_print", false);
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
