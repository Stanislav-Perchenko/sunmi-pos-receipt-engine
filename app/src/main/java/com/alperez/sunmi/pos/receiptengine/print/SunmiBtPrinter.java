package com.alperez.sunmi.pos.receiptengine.print;

import android.content.Context;

import java.util.Collection;

public final class SunmiBtPrinter {

    private static SunmiBtPrinter instance;

    public static SunmiBtPrinter getInstance(Context ctx) {
        if (instance == null) {
            synchronized (SunmiBtPrinter.class) {
                if (instance == null) {
                    instance = new SunmiBtPrinter(ctx);
                }
            }
        }
        return instance;
    }


    private SunmiBtPrinter(Context ctx) {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    public void print(Collection<byte[]> data, PrintResultCallback callback) {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
}
