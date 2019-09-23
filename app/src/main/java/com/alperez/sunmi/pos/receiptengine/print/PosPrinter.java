package com.alperez.sunmi.pos.receiptengine.print;

import android.content.Context;

import java.util.Collection;

public interface PosPrinter {
    /**
     * Returns a set of static parameters of this printer
     * @return
     */
    PosPrinterParams getPrinterParams();


    /**
     * This method puts a new print job into queue and start worker thread if necessary
     * @param data - raw data to be sent to the printer
     * @param callback - use this for print result notifications
     */
    void print(Collection<byte[]> data, PrintResultCallback callback);


    /**
     * This method notifies the worker thread to stop. All jobs waiting in the queue are deleted.
     */
    void shutdown();

    static PosPrinter getSunmiInternalBTPrinter(Context ctx) {
        return SunmiBtPrinter.getInstance(ctx);
    }
}
