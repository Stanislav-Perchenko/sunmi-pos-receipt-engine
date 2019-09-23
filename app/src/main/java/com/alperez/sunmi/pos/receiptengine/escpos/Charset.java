package com.alperez.sunmi.pos.receiptengine.escpos;

public enum Charset {
    CP437(false, 0, "CP437", "USA, Standard Europe"),
    CP850(false, 2, "CP850", "Multilingual"),
    CP860(false, 3, "CP860", "Portuguese"),
    CP863(false, 4, "CP863", "Canadian-French"),
    CP865(false, 5, "CP865", "Nordic"),
    CP857(false, 13, "CP857", "Turkish"),
    CP737(false, 14, "CP737", "Greek"),
    CP928(false, 15, "CP928", "ISO8859-7: Greek"),
    WIN1252(false, 16, "Windows-1252", "Windows-1252"),
    CP866(false, 17, "CP866", "Cyrillic #2"),
    CP852(false, 18, "CP852", "Latin 2"),
    CP858(false, 19, "CP858", "Euro"),
    CP874(false, 21, "CP874", "Thai Character Code 11"),
    WIN775(false, 33, "Windows-775", "Windows-775"),
    CP855(false, 34, "CP855", "Cyrillic"),
    CP862(false, 36, "CP862", "Hebrew"),
    CP864(false, 37, "CP864", "Arabic"),
    GB18030(true, 0, "GB18030", "GBK simple Chinese"),
    BIG5(true, 1, "BIG5", "BIG5 traditional Chines"),
    KSC5601(true, 2, "KSC5601", "KSC5601 korean"),
    UTF8(true, 0xFF, "utf-8", "UTF-8");


    private final boolean multiByte;
    private final byte sunmiCode;
    private final String encodingStdName;
    private final String codepageDescription;

    Charset(boolean multiByte, int sunmiCode, String encodingStdName, String codepageDescription) {
        this.multiByte = multiByte;
        this.sunmiCode = (byte) sunmiCode;
        this.encodingStdName = encodingStdName;
        this.codepageDescription = codepageDescription;
    }

    public boolean isMultiByte() {
        return multiByte;
    }

    public byte getSunmiCode() {
        return sunmiCode;
    }

    public String getEncodingStdName() {
        return encodingStdName;
    }

    public String getCodepageDescription() {
        return codepageDescription;
    }
}
