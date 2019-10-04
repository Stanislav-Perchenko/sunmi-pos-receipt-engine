package com.alperez.sunmi.pos.receiptengine.template;

import androidx.annotation.NonNull;

import com.alperez.sunmi.pos.receiptengine.escpos.Charset;
import com.alperez.sunmi.pos.receiptengine.escpos.ESCUtils;
import com.alperez.sunmi.pos.receiptengine.parammapper.ParameterValueMapper;
import com.alperez.sunmi.pos.receiptengine.print.PosPrinterParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    float getColumnsWidthPercent(int colIndex) {
        return columnsWidthPercent[colIndex];
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
        List<byte[]> dataset = new LinkedList<>();
        if (printerParams.isUnidirectionPrintSupported()) dataset.add(ESCUtils.setUnidirectionalPrintModeEnabled(true));

        dataset.add(ESCUtils.setLineSpacing(printerParams.reducedLineSpacingValue()));
        dataset.add(ESCUtils.setBoldEnabled(false));
        dataset.add(ESCUtils.setTextAlignment(TextAlign.ALIGN_LEFT));

        int sc_w = 1;
        if (sc_w < printerParams.characterScaleWidthLimits()[0]) sc_w = printerParams.characterScaleWidthLimits()[0];
        else if (sc_w > printerParams.characterScaleWidthLimits()[1]) sc_w = printerParams.characterScaleWidthLimits()[1];
        byte[] tableContent = buildTableContent(charset, printerParams.lineLengthFromScaleWidth(sc_w));
        dataset.add(tableContent);

        if (printerParams.isUnidirectionPrintSupported()) dataset.add(ESCUtils.setUnidirectionalPrintModeEnabled(false));
        return dataset;
    }

    private byte[] buildTableContent(Charset charset, final int maxPrintW) throws UnsupportedEncodingException {
        try {
            final int n_spaces = nColumns + 1;
            final int totalRowContentW = maxPrintW - n_spaces;
            int[] colWww = new int[nColumns];

            int check_sum = 0;
            for (int i=0; i<nColumns; i++) check_sum += (colWww[i] = Math.round(totalRowContentW * getColumnsWidthPercent(i)/100));
            if(check_sum != totalRowContentW) throw new IOException(String.format("Bad column width sum. Required - %d. Got - %d", totalRowContentW, check_sum));

            ByteArrayOutputStream bos = new ByteArrayOutputStream(512);

            for (int ri = 0; ri < nRows; ri++) {
                if (ri == 0) {
                    printTableOpenFrame(bos, colWww);
                } else {
                    printTableInnerFrame(bos, colWww);
                }
                printRowContent(bos, cells[ri], colWww, charset);
            }
            printTableCloseFrame(bos, colWww);


            return bos.toByteArray();
        } catch (Exception e) {
            return String.format("\n{table error: %s}\n", e.getMessage()).getBytes(charset.getEncodingStdName());
        }
    }



    private void printRowContent(OutputStream os, String[] cells, int[] col_www, Charset charset) throws IOException {

        String[][] splitContent = new String[cells.length][];
        int splitH = 1;
        int totalW = cells.length + 1;
        for (int i=0; i<cells.length; i++) {
            totalW += col_www[i];
            splitContent[i] = cells[i].split("\\n");
            if (splitContent[i].length > splitH) splitH = splitContent[i].length;
        }

        char[][] all_chars = new char[splitH][];
        for (int i=0; i<splitH; i++) {
            char[] ccc = new char[totalW];
            int index = 0;
            for (int j=0; j<cells.length; j++) {
                ccc[index++] = '\u2502';
                Arrays.fill(ccc, index, index + col_www[j], ' ');
                index += col_www[j];
            }
            ccc[index] = '\u2502';
            all_chars[i] = ccc;
        }


        for (int colIndex = 0; colIndex < cells.length; colIndex ++) {
            final int colW = col_www[colIndex];
            final TextAlign colAlign = this.columnsAlign[colIndex];
            final int startPad = this.columnsStartPadding[colIndex];

            String[] splt_column = splitContent[colIndex];
            for (int rowIndex=0; rowIndex<splt_column.length; rowIndex++) {
                String txt = splt_column[rowIndex];
                int nBefore;
                if (colAlign == TextAlign.ALIGN_LEFT) {
                    if (txt.length() > (colW-startPad)) txt = txt.substring(0, colW-startPad);
                    nBefore = startPad;
                } else if (colAlign == TextAlign.ALIGN_CENTER) {
                    if (txt.length() > colW) txt = txt.substring(0, colW);
                    nBefore = (colW - txt.length()) / 2;
                } else if (colAlign == TextAlign.ALIGN_RIGHT) {
                    if (txt.length() > (colW-startPad)) txt = txt.substring(0, colW-startPad);
                    nBefore = colW - txt.length() - startPad;
                } else {
                    if (txt.length() > colW) txt = txt.substring(0, colW);
                    nBefore = 0;
                }

                int inCellIndex = 1;
                for (int i=0; i<colIndex; i++) {
                    inCellIndex += col_www[i];
                    inCellIndex ++;
                }
                inCellIndex += nBefore;

                for (int i=0; i<txt.length(); i++) {
                    all_chars[rowIndex][inCellIndex ++] = txt.charAt(i);
                }
            }
        }

        StringBuilder sb = new StringBuilder(splitH*totalW*2+1);
        for (int ri = 0; ri < splitH; ri++) {
            sb.append(all_chars[ri]);
            sb.append('\n');
        }

        os.write(sb.toString().getBytes(charset.getEncodingStdName()));

    }

    private void printTableOpenFrame(OutputStream os, int[] col_www) throws IOException {
        for (int ci = 0; ci < col_www.length; ci++) {
            os.write((ci == 0) ? 0xDA : 0xC2);
            for (int j = 0; j < col_www[ci]; j++) os.write(0xC4);
        }
        os.write(0xBF);
    }

    private void printTableInnerFrame(OutputStream os, int[] col_www) throws IOException {
        for (int ci = 0; ci < col_www.length; ci++) {
            os.write((ci == 0) ? 0xC3 : 0xC5);
            for (int j = 0; j < col_www[ci]; j++) os.write(0xC4);
        }
        os.write(0xB4);
    }

    private void printTableCloseFrame(OutputStream os, int[] col_www) throws IOException {
        for (int ci = 0; ci < col_www.length; ci++) {
            os.write((ci == 0) ? 0xC0 : 0xC1);
            for (int j = 0; j < col_www[ci]; j++) os.write(0xC4);
        }
        os.write(0xD9);
    }
}
