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

    private static class ColumnTemplate {
        final String title;
        final TextAlign titleAlign;
        final boolean isTitleBold;
        final TextAlign contentAlign;
        final boolean isContentBold;
        final int minWidth, maxWidth;

        ColumnTemplate(JSONObject jObj) throws JSONException {
            title = jObj.getString("title");
            titleAlign = TextAlign.fromJson(jObj.optString("title_align", TextAlign.ALIGN_LEFT.getJsonValue()));
            isTitleBold = jObj.optBoolean("title_bold", false);
            contentAlign = TextAlign.fromJson(jObj.optString("content_align", TextAlign.ALIGN_LEFT.getJsonValue()));
            isContentBold = jObj.optBoolean("content_bold", false);
            minWidth = jObj.optInt("min_width", 0);
            maxWidth = jObj.optInt("max_width", 0);
        }
    }

    private static class RowTotalTemplate {
        final String title;
        final boolean isBold;
        final TextAlign titleAlign;
        final TextAlign valueAlign;

        RowTotalTemplate(JSONObject jObj) throws JSONException {
            title = jObj.getString("title");
            isBold = jObj.optBoolean("bold", false);
            titleAlign = TextAlign.fromJson(jObj.optString("align_title", TextAlign.ALIGN_LEFT.getJsonValue()));
            valueAlign = TextAlign.fromJson(jObj.optString("align_value", TextAlign.ALIGN_LEFT.getJsonValue()));
        }
    }

    private final String json;
    private final ColumnTemplate columnItemsTemplate;
    private final ColumnTemplate columnWeightTemplate;
    private final ColumnTemplate columnAmountTemplate;
    private final RowTotalTemplate rowTotalTemplate;
    private final boolean isPriceSeparatedThousands;
    private final int optItemsStartPadding;

    private final GoodsCollectDataItem[] collectedItems;


    public GoodsCollectTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        columnItemsTemplate = new ColumnTemplate(jObj.getJSONObject("column_items"));
        columnWeightTemplate = new ColumnTemplate(jObj.getJSONObject("column_weight"));
        columnAmountTemplate = new ColumnTemplate(jObj.getJSONObject("column_amount"));
        rowTotalTemplate = new RowTotalTemplate(jObj.getJSONObject("row_total"));
        isPriceSeparatedThousands = jObj.optBoolean("price_separate_thousands", false);
        optItemsStartPadding = jObj.optInt("opt_items_start_padding", 0);

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
