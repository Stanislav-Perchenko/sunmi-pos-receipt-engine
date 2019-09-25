package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.parammapper.JsonMappableEntity;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

public class GoodsCollectTemplateItem extends BaseTemplateItem {

    private final String json;

    private final GoodsCollectDataItem[] collectedItems;

    public GoodsCollectTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        collectedItems = valueMapper.mapObjectArrayValue(jObj.getString("data"));
    }



    @Override
    public String getJson() {
        return json;
    }


    static final String TYPE_JSON_VALUE = "goods_collect_table";

    @Override
    public String getTypeJsonValue() {
        return TYPE_JSON_VALUE;
    }


    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        return new ArrayList<>();
    }


    /**********************************************************************************************/
    public static class GoodsCollectDataItem extends JsonMappableEntity {
        final String categoryName;
        final int collectedWeight;
        final int amount;
        final int currencyScale;

        public GoodsCollectDataItem(JSONObject jObj) throws JSONException {
            super(jObj);
            this.categoryName = jObj.getString("full_category_name");
            this.collectedWeight = jObj.getInt("weight_kg");
            this.amount = jObj.getInt("amount");
            this.currencyScale = jObj.getInt("currency_scale");
        }
    }
}
