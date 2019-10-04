package com.alperez.sunmi.pos.receiptengine.parammapper;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.sunmi.pos.receiptengine.template.GoodsCollectTemplateItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;

public class ParamMapperImpl implements ParameterValueMapper {
    @Nullable
    private JSONObject dataJson;

    ParamMapperImpl(@Nullable JSONObject dataJson) {
        this.dataJson = dataJson;
    }

    @NonNull
    @Override
    public String mapTextValue(@NonNull String template) throws JSONException {
        StringBuilder result_builder = new StringBuilder();
        final int L = template.length();
        int position = 0;
        do {
            int start = template.indexOf('{', position);
            if (start < position) {
                result_builder.append(template.substring(position));
                position = L;
            } else {
                result_builder.append(template.substring(position, start));
                position = start;

                int end = template.indexOf('}', position);
                if (end < start) {
                    throw new JSONException("No closing brace (}) for parameter - "+template);
                } else {
                    position = end + 1;
                    String param = template.substring(start, position);
                    result_builder.append(mapCleanParamToText(param));
                }
            }
        } while (position < L);



        return result_builder.toString();
    }

    @NonNull
    private String mapCleanParamToText(@NonNull String param) {
        if ((param.charAt(0) == '{') || (param.charAt(param.length()-1) == '}')) {
            int start = (param.charAt(0) == '{') ? 1 : 0;
            int end = (param.charAt(param.length()-1) == '}') ? param.length()-1 : param.length();
            param = param.substring(start, end);
        }

        try {
            int typeDelimIndex = param.indexOf(':');
            if (typeDelimIndex <= 0) throw new JSONException("");

            final String path = param.substring(0, typeDelimIndex).trim();
            final String type = param.substring(typeDelimIndex+1).trim();

            final String[] p_segs = path.split("\\.");

            JSONObject currentJson = dataJson;
            String result = param;
            for (int i=0; i<p_segs.length; i++) {
                int arrIndex = optArrayIndex(p_segs[i]);
                if (i < (p_segs.length-1)) {
                    //Extract intermediate objects
                    if (arrIndex < 0) {
                        currentJson = Objects.requireNonNull(currentJson).getJSONObject(p_segs[i]);
                    } else {
                        JSONArray jArr = Objects.requireNonNull(currentJson).getJSONArray(getCleanArrayName(p_segs[i]));
                        currentJson = jArr.getJSONObject(arrIndex);
                    }
                } else {
                    //Extract final object
                    String extractedValue = (arrIndex < 0)
                            ? extractFinalValue(currentJson, p_segs[i], type)
                            : extractFinalValue(Objects.requireNonNull(currentJson).getJSONArray(getCleanArrayName(p_segs[i])), arrIndex, type);
                    if (extractedValue != null) result = extractedValue;
                }
            }
            return result;
        } catch (JSONException | IndexOutOfBoundsException e) {
            return param;
        }
    }

    private String getCleanArrayName(String arrWithIndex) {
        int pos = arrWithIndex.indexOf('[');
        return (pos < 0) ? arrWithIndex : arrWithIndex.substring(0, pos);
    }

    private String extractFinalValue(JSONArray jArr, int index, String paramType) throws JSONException {
        if (paramType.equals("text")) {
            return jArr.getString(index);
        } else if (paramType.equals("int")) {
            return Integer.toString(jArr.getInt(index));
        } else if (paramType.equals("long")) {
            return Long.toString(jArr.getLong(index));
        } else if (paramType.startsWith("timestamp")) {
            long timestamp = jArr.getLong(index);
            String tmpl = paramType.substring(10, paramType.length() - 1);
            return String.format(tmpl, new Date(timestamp));
        } else {
            return null;
        }
    }

    private String extractFinalValue(JSONObject jObj, String paramName, String paramType) throws JSONException {
        if (paramType.equals("text")) {
            return jObj.getString(paramName);
        } else if (paramType.equals("int")) {
            return Integer.toString(jObj.getInt(paramName));
        } else if (paramType.equals("long")) {
            return Long.toString(jObj.getLong(paramName));
        } else if (paramType.startsWith("timestamp")) {
            long timestamp = jObj.getLong(paramName);
            String tmpl = paramType.substring(10, paramType.length() - 1);
            return String.format(tmpl, new Date(timestamp));
        } else {
            return null;
        }
    }

    private int optArrayIndex(String pName) {
        int start = pName.indexOf('[');
        if (start > 0) {
            int end = pName.indexOf(']', start);
            if (end > start) {
                try {
                    return Integer.parseInt(pName.substring(start+1, end));
                } catch (NumberFormatException e) { /* Ignore */ }
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public byte[] mapByteArrayValue(@NonNull String template) {
        try {
            String param = template.substring(template.indexOf('{')+1, template.lastIndexOf('}'));
            if ((param.indexOf('{') >= 0) || (param.indexOf('}') >= 0)) {
                throw new JSONException("only single parameter is allowed - "+param);
            }

            int typeDelimIndex = param.indexOf(':');
            if (typeDelimIndex <= 0) throw new JSONException("");

            final String path = param.substring(0, typeDelimIndex).trim();
            final String type = param.substring(typeDelimIndex+1).trim();
            if (!"byte[]".equals(type) && !"base64".equals(type)) {
                throw new JSONException("A type of 'byte[]' or 'base64' are allowed. Got - "+type);
            }

            final String[] p_segs = path.split("\\.");
            JSONObject currentJson = dataJson;
            byte[] result = new byte[0];
            for (int i=0; i<p_segs.length; i++) {

                if (i < (p_segs.length-1)) {
                    //Extract intermediate objects
                    int arrIndex = optArrayIndex(p_segs[i]);
                    if (arrIndex < 0) {
                        currentJson = Objects.requireNonNull(currentJson).getJSONObject(p_segs[i]);
                    } else {
                        JSONArray jArr = Objects.requireNonNull(currentJson).getJSONArray(getCleanArrayName(p_segs[i]));
                        currentJson = jArr.getJSONObject(arrIndex);
                    }
                } else {
                    if (type.equals("byte[]")) {
                        JSONArray jArr = Objects.requireNonNull(currentJson).getJSONArray(p_segs[i]);
                        result = new byte[jArr.length()];
                        for (int j = 0; j < result.length; j++) result[j] = (byte) jArr.getInt(j);
                    } else //noinspection ConstantConditions
                        if (type.equals("base64")) {
                        String base64 = Objects.requireNonNull(currentJson).getString(p_segs[i]);
                        result = Base64.decode(base64, Base64.NO_WRAP | Base64.NO_PADDING);
                    }
                }
            }
            return result;
        } catch (JSONException | RuntimeException e) {
            return new byte[0];
        }
    }


    @Override
    public <T extends JsonMappableEntity> T[] mapObjectArrayValue(@NonNull String template) throws JSONException {
            String param = template.substring(template.indexOf('{')+1, template.lastIndexOf('}'));
            if ((param.indexOf('{') >= 0) || (param.indexOf('}') >= 0)) {
                throw new JSONException("only single parameter is allowed - "+param);
            }

            int typeDelimIndex = param.indexOf(':');
            if (typeDelimIndex <= 0) throw new JSONException("");

            final String path = param.substring(0, typeDelimIndex).trim();
            final String type = param.substring(typeDelimIndex+1).trim();

            final String[] p_segs = path.split("\\.");
            JSONObject currentJson = dataJson;
            T[] result = null;
            for (int i=0; i<p_segs.length; i++) {

                if (i < (p_segs.length-1)) {
                    //Extract intermediate objects
                    int arrIndex = optArrayIndex(p_segs[i]);
                    if (arrIndex < 0) {
                        currentJson = Objects.requireNonNull(currentJson).getJSONObject(p_segs[i]);
                    } else {
                        JSONArray jArr = Objects.requireNonNull(currentJson).getJSONArray(getCleanArrayName(p_segs[i]));
                        currentJson = jArr.getJSONObject(arrIndex);
                    }
                } else {
                    JSONArray jArr = Objects.requireNonNull(currentJson).getJSONArray(p_segs[i]);


                    //noinspection SwitchStatementWithTooFewBranches
                    switch (type) {
                        case "CollectedGoodItem[]":
                            GoodsCollectTemplateItem.GoodsCollectDataItem[] data = new GoodsCollectTemplateItem.GoodsCollectDataItem[jArr.length()];
                            for (int j=0; j<jArr.length(); j++) {
                                data[j] = new GoodsCollectTemplateItem.GoodsCollectDataItem(jArr.getJSONObject(j));
                            }
                            //noinspection unchecked
                            result = (T[])data;
                            break;
                        default:
                            throw new JSONException("Unsupported object type - "+type);
                    }
                }
            }
            return result;
    }
}
