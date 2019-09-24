package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

final class TableTemplateItem extends BaseTemplateItem {
    private final String json;

    private final int nColumns;
    private final int[] columnsWidthPercent;
    private final TextAlign[] columnsAlign;
    private final int[] columnsStartPadding;
    private final int nRows;
    private final String[][] cells;



    TableTemplateItem(JSONObject jObj, @NonNull ParameterValueMapper valueMapper) throws JSONException {
        super(jObj);
        json = jObj.toString();
        nColumns = jObj.getInt("n_columns");

        JSONArray jColWww = jObj.getJSONArray("col_widths");
        if (jColWww.length() != nColumns) {
            throw new JSONException("Wrong column width set - "+jColWww.toString());
        } else {
            columnsWidthPercent = new int[nColumns];
            int sumW = 0;
            for (int i=0; i<nColumns; i++) sumW += columnsWidthPercent[i] = jColWww.getInt(i);
            if (sumW != 100) throw new JSONException("Sum of columns width is not 100 - "+jColWww.toString());
        }

        JSONArray jColAligns = jObj.optJSONArray("col_aligns");
        if (jColAligns == null) {
            columnsAlign = new TextAlign[nColumns];
            for (int i=0; i<nColumns; i++) columnsAlign[i] = TextAlign.ALIGN_LEFT;
        } else if (jColAligns.length() != nColumns) {
            throw new JSONException("Wrong column align set - "+jColAligns.toString());
        } else {
            columnsAlign = new TextAlign[nColumns];
            for (int i=0; i<nColumns; i++) columnsAlign[i] = TextAlign.fromJson(jColAligns.getString(i));
        }

        JSONArray jColPads = jObj.optJSONArray("col_start_padding");
        if (jColPads == null) {
            columnsStartPadding = new int[nColumns];
        } else if (jColPads.length() != nColumns) {
            throw new JSONException("Wrong column start padding set - "+jColPads.toString());
        } else {
            columnsStartPadding = new int[nColumns];
            for (int i=0; i<nColumns; i++) columnsStartPadding[i] = jColPads.getInt(i);
        }

        //-------  Parse Rows/Cells  ---------
        JSONArray jRows = jObj.getJSONArray("rows");
        nRows = jRows.length();
        cells = new String[nRows][nColumns];
        for (int i=0; i<nRows; i++) {
            JSONArray jCells = jRows.getJSONObject(i).getJSONArray("cells");
            for (int j=0; j<nColumns; j++) {
                String v = jCells.optString(j);
                cells[i][j] = valueMapper.mapTextValue((v == null) ? "" : v);
            }
        }
    }


    public String getJson() {
        return json;
    }

    public int getNRows() {
        return nRows;
    }

    public int getNColumns() {
        return nColumns;
    }

    public int getColumnWidthPercent(int colIndex) {
        return columnsWidthPercent[colIndex];
    }

    public TextAlign getColumnAlign(int colIndex) {
        return columnsAlign[colIndex];
    }

    public int getColumnStartPadding(int colIndex) {
        return columnsStartPadding[colIndex];
    }

    public String getCellValue(int rowIndex, int colIndex) {
        return cells[rowIndex][colIndex];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableTemplateItem that = (TableTemplateItem) o;
        return nColumns == that.nColumns &&
                nRows == that.nRows &&
                Arrays.equals(columnsWidthPercent, that.columnsWidthPercent) &&
                Arrays.equals(columnsAlign, that.columnsAlign) &&
                Arrays.equals(columnsStartPadding, that.columnsStartPadding) &&
                Arrays.equals(cells, that.cells);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nColumns, nRows);
        result = 31 * result + Arrays.hashCode(columnsWidthPercent);
        result = 31 * result + Arrays.hashCode(columnsAlign);
        result = 31 * result + Arrays.hashCode(columnsStartPadding);
        result = 31 * result + Arrays.hashCode(cells);
        return result;
    }

    static final String TYPE_JSON_VALUE = "table";

    @Override
    public String getTypeJsonValue() {
        return TYPE_JSON_VALUE;
    }


    /************************  Build ESC/POS printer raw data  ************************************/
    @Override
    public Collection<byte[]> getPrinterRawData(Charset charset, PosPrinterParams printerParams) throws UnsupportedEncodingException {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        List<byte[]> ret = new LinkedList<>();
        ret.add(ESCUtils.setUnidirectionalPrintModeEnabled(true));


        return ret;
    }
}
