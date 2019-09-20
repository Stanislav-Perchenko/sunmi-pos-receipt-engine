package com.alperez.sunmi.pos.receiptengine.escpos;

public enum Charset {
    CP437(false, 0, "USA, Standard Europe"),
    CP850(false, 2, "Multilingual"),
    CP860(false, 3, "Portuguese"),
    CP863(false, 4, "Canadian-French"),
    CP865(false, 5, "Nordic"),
    CP857(false, 13, "Turkish"),
    CP737(false, 14, "Greek"),
    CP928(false, 15, "ISO8859-7: Greek"),
    WIN1252(false, 16, "Windows-1252"),
    CP866(false, 17, "Cyrillic #2"),
    CP852(false, 18, "Latin 2"),
    CP858(false, 19, "Euro"),
    CP874(false, 21, "Thai Character Code 11"),
    WIN775(false, 33, "Windows-775"),
    CP855(false, 34, "Cyrillic"),
    CP862(false, 36, "Hebrew"),
    CP864(false, 37, "Arabic"),
    GB18030(true, 0, "GBK simple Chinese"),
    BIG5(true, 1, "BIG5 traditional Chines"),
    KSC5601(true, 2, "KSC5601 korean"),
    UTF8(true, 0xFF, "UTF-8");


    private final boolean multiByte;
    private final byte sunmiCode;
    private final String codepageDescription;

    Charset(boolean multiByte, int sunmiCode, String codepageDescription) {
        this.multiByte = multiByte;
        this.sunmiCode = (byte) sunmiCode;
        this.codepageDescription = codepageDescription;
    }

    public boolean isMultiByte() {
        return multiByte;
    }

    public byte getSunmiCode() {
        return sunmiCode;
    }

    public String getCodepageDescription() {
        return codepageDescription;
    }
}
